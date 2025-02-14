/* (c) https://github.com/MontiCore/monticore */

package de.monticore.io.paths;

import de.monticore.AmbiguityException;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.MontiCoreClassLoader;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A ModelPath encapsulates the domain of accessible models inside the running
 * language tool.
 */
public final class MCPath {

  protected final Map<URLClassLoader, URL> classloaderMap = new LinkedHashMap<>();

  public MCPath() { }

  public MCPath(Collection<Path> entries) {
    entries.stream()
        .map(MCPath::toURL)
        .filter(Optional::isPresent)
        .map(Optional::get)
        // parent class loader MUST BE null here!
        // otherwise we would start to resolve from the system class path (or
        // worse) unknowingly
        .forEach(url -> classloaderMap.put(new MontiCoreClassLoader(new URL[] { url }, null), url));
  }

  public MCPath(Path... entries) {
    this(Arrays.asList(entries));
  }

  public MCPath(String... entries) {
    this(Arrays.stream(entries).map(Paths::get).collect(Collectors.toList()));
  }

  public void addEntry(Path entry) {
    Optional<URL> url = toURL(entry);
    if(url.isPresent() && !classloaderMap.containsValue(url.get())){
      classloaderMap.put(new MontiCoreClassLoader(new URL[] { url.get() }, null), url.get());
    }
  }

  public void removeEntry(Path entry) {
    Optional<URLClassLoader> urlClassLoader = toURL(entry)
        .flatMap(url -> classloaderMap.entrySet().stream()
            .filter(e -> e.getValue().equals(url))
            .findFirst())
        .map(Map.Entry::getKey);
    urlClassLoader.ifPresent(classloaderMap::remove);
  }

  public Collection<Path> getEntries() {
    return classloaderMap.values().stream()
        .map(MCPath::toPath)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  public boolean isEmpty(){
    return classloaderMap.isEmpty();
  }

  /**
   * Method for calculating a list of files located in an entry of the passed model path,
   * with the passed qualified model name, and the passed regular expression over the file extension.
   * <p>
   * Example: for a model path comprising two entries "src/test/resources" and "target", the
   * qualified model name "foo.bar.Car", and the file extension regular expression "*sym", the
   * result of this method could be a list with three files:
   * "src/test/resources/foo/bar/Car.fdsym"
   * "src/test/resources/foo/bar/Car.cdsym"
   * "target/foo/bar/Car.fdsym"
   *
   * @param fileExtRegEx
   * @return
   */
  public Optional<URL> find(String qualifiedName, String fileExtRegEx) {
    // calculate the folderPath (e.g., "foo/bar") and fileNameRegEx (e.g., "Car.*sym")
    String folderPath = Names.getPathFromQualifiedName(qualifiedName);
    String fileNameRegEx = Names.getSimpleName(qualifiedName) + "\\." + fileExtRegEx;

    // initialize a file filter filtering for the regular expression
    FileFilter filter = new RegexFileFilter(fileNameRegEx);

    List<URL> resolvedURLs = new ArrayList<>();
    // iterate MCPath entries and check whether folder path exists within these
    for (Path p : getEntries()) {
      if(p.toString().endsWith(".jar")){
        String path = "/" + folderPath.replaceAll("\\\\", "/") + "/" + fileNameRegEx;
        GlobExpressionEvaluator evaluator = new GlobExpressionEvaluator(path, getJarFS(p.toFile()), true);
        resolvedURLs.addAll(evaluator.evaluate(p.toFile()).stream().map(uri -> {
          try {
            return uri.toURL();
          } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
          }
        }).collect(Collectors.toList()));
      }
      File folder = p.resolve(folderPath).toFile(); //e.g., "src/test/resources/foo/bar"
      if (folder.exists() && folder.isDirectory()) {
        // perform the actual file filter on the folder and collect result
        Arrays.stream(folder.listFiles(filter))
            .map(f -> toURL(folder.toPath().resolve(f.getName())))
            .filter(Optional::isPresent)
            .forEach(f -> resolvedURLs.add(f.get()));
      }
    }

    if (1 == resolvedURLs.size()) {
      return Optional.of(resolvedURLs.get(0));
    }
    else if (1 < resolvedURLs.size()) {
      reportAmbiguity(resolvedURLs, fileNameRegEx);
    }
    return Optional.empty();
  }

  /**
   * Searches for a path in all entries. The result is either the fully qualified
   * path or an empty Optional. An error is logged if multiple, ambiguous results
   * are found.
   *
   * @param path a relative path, e.g, a/b/C.foo, to search in all MCPath entries
   * @return the URL representing the unique fully qualified name of the result,
   * if it exists
   * @throws AmbiguityException if the search locates multiple potentially
   *                            matching models
   */
  public Optional<URL> find(String path) {
    String fixedPath = path.replaceAll("\\" + File.separator, "/");

    List<URL> resolvedURLs = classloaderMap.keySet().stream()
        .map(classloader -> FileReaderWriter.getResource(classloader, fixedPath))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .distinct()
        .collect(Collectors.toList());

    if (1 == resolvedURLs.size()) {
      return Optional.of(resolvedURLs.get(0));
    }
    else if (1 < resolvedURLs.size()) {
      reportAmbiguity(resolvedURLs, fixedPath);
    }
    return Optional.empty();
  }

  @Override
  public String toString() {
    String result = "[";
    result = result + this.classloaderMap.values().stream()
        .map(URL::toString)
        .collect(Collectors.joining(", "));
    return result + "]";
  }

  public static Optional<Path> toPath(URL url) {
    try {
      return Optional.of(Paths.get(url.toURI()));
    }
    catch (URISyntaxException e) {
      Log.error("0xA1025 The entry " + url + " in the MCPath was invalid.", e);
      return Optional.empty();
    }
  }

  public static Optional<URL> toURL(Path p) {
    try {
      return Optional.of(p.toUri().toURL());
    }
    catch (MalformedURLException e) {
      Log.error("0xA1022 The entry " + p + " in the MCPath was invalid.", e);
      return Optional.empty();
    }
  }

  protected static void reportAmbiguity(List<URL> resolvedURLs, String path) {
    StringBuilder ambiguityArray = new StringBuilder("{");
    String sep = "";
    for (URL url : resolvedURLs) {
      ambiguityArray.append(sep);
      sep = ",\n";
      ambiguityArray.append(url.toString());
    }
    ambiguityArray.append("}");
    Log.error(
        "0xA1294 The following entries for the file `" + path + "` are ambiguous:"
            + "\n" + ambiguityArray.toString());
  }

  public void close(){
    classloaderMap.keySet().stream().forEach(c -> {
      try {
        c.close();
      }
      catch (IOException e) {
        Log.error("0xA1035 An exception occurred while trying to close a class loader!", e);
      }
    });
  }

  // A List of all file systems opened for jars.
  private static Map<File, FileSystem> openedJarFileSystems = new HashMap<>();

  public static FileSystem getJarFS(File jar) {
    if(openedJarFileSystems.containsKey(jar)){
      FileSystem fs = openedJarFileSystems.get(jar);
      if(fs.isOpen()){
        return fs;
      }
    }

    try {
      FileSystem fileSystem = FileSystems.newFileSystem(jar.toPath(), MCPath.class.getClassLoader());
      openedJarFileSystems.put(jar, fileSystem);
      return fileSystem;
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return FileSystems.getDefault();
  }

  /**
   * Closes all still opened file systems created with {@link MCPath#getJarFS(File)}.
   */
  public static void closeAllJarFileSystems(){
    for (FileSystem fs : openedJarFileSystems.values()) {
      if (fs.isOpen()) {
        try {
          fs.close();
        } catch (IOException e){
          e.printStackTrace();
        }
      }
    }
    openedJarFileSystems.clear();
  }
}
