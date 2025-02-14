<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Best Practices - Designing Languages

[MontiCore](https://www.monticore.de) provides a number of options to design 
languages, access and modify the abstract syntax tree, and produce output files.

Some general questions on how to design a complete languages are addressed here. 

## **Designing A Language**

### Correct language vs. superset?
* When you know that the incoming model will be correct, because they are generated
  by algorithm, you can decide to pass a (slight) superset 
* This may simplify the development process for two reasons: 
  (a) you may derive a simpler grammar and (b) you may omit definitions of 
  context conditions.
* But beware: (a) situations may change and manually changed models might come in
  or (b) the is adapted by an ill-behaving pre-processor or (c) the model
  may come in a wrong version.
* This applies mainly for unreadable languages, such as JSON or XML.
* Defined by: BR


### Versioning an evolving language?
* When languages evolve, models may become invalid, because 
  certain (now obligatory) parts are missing, or old keywords are used.
* We generally believe that a language that is made for long-lasting 
  models should not embody its version in the models (i.e. like Java, C++ and 
  other GPLs and unlike XML dialects).
* When evolving a language, you should only evolve it in conservative form, i.e.
  * All new elements are optional by `.?`, `.*` or offer new alternatives `(old | new)`
  * Old elements or keywords are not simply removed, but 
    forbidden by coco warnings, marking them as deprecated for a while. 
* Downward compatibility of newer models, however, is not useful. 
  We can safely enforce developers should normally use the newest 
  versions of their tools.
* Defined by: BR



## **Language Design in the Large**


### Making Transitively Inherited Grammars Explicit?
* When the grammar inclusion hierarchy becomes larger, there will be redundancy.
  In:
  ```
    grammar A { .. } ;
    grammar B extends A { .. } ;
    grammar C extends A,B { .. } ;
    grammar D extends B { .. } ;
  ```
  Grammars `C` and `D` actually include the same nonterminals.
* If `A` is made explicit, you have more information right at hand, but also
  larger grammars. It is a matter of taste.
* A recommendation: when you use nonterminals from A explicitly, then also 
  make the extension explicit. However, be consistent.


### How to Achieve Modularity (in the Sense of Decoupling)
* Modularity in general is an important design principle.
  In the case of model-based code generation, modularity involves the following 
  dimensions:
    1. Modelling languages
    2. Models
    3. Generator
    4. Generated code
    5. Runtime-Environment (RTE) including imported standard libraries
    6. Software architecture (of the overall system), software stack
* These dimensions are not orthogonal, but also not completely interrelated.
  The actual organisation will depend on the form of project.
* A weak form of modularity would be to organize things in
  well understood substructures such as packages. 
  A deeper form of modularity deals with possibility for individual *reuse* 
  and thus an explicit *decoupling* of individual components. We aim for 
  decoupling (even if developed in the same git project).
* Modularity also deals with *extensibility* and *adaptation*.
* A principle for *adaptation* for the *generator*, 
  the *generated code*, and the *RTE* is to design each of them
  like a *framework* with explicit extension points.
  Extension points may be (empty) hook methods to be filled, Java interfaces
  to be implemented and their objects injected to the code e.g., via 
  factories, builders or simply method parameters.
* A principle for *modularity* for the *generator*, 
  the *generated code*, and the *RTE* is to design parts of them as 
  independent library functions (or larger: components) that can be used if needed.
* We recommend to modularize whenever complexity overwhelms or extensibility and
  adaptability are important:
    1. MontiCore has powerful techniques for adaptation, extension and 
       composition of *modelling languages* (through their grammars). See the
       [reference manual](https://monticore.de/MontiCore_Reference-Manual.2017.pdf).
    2. MontiCore has powerful techniques for the *aggregation of models* --
       using the same principles as programming languages, namely allowing to keep 
       the models independent (and thus storable, versionable, reusable) artifacts,
       while they are semantically and through the generator technology well integrated. 
       The appropriate approach is based on *using* foreign models, e.g., through 
       `import` statements and sharing *symbol* infrastructures as described in the
       [reference manual](https://monticore.de/MontiCore_Reference-Manual.2017.pdf).
    3. The generator provides (a) many Java classes and methods that can be overridden
       (b) Freemarker templates hook points to extend and replace templates, and (c)
       can be customized using a groovy script.
       The generator itself is often structured along the software architecture / stack,
       e.g., in frontend, application backend, database, transport layer, etc.
    4. The generated code must be designed appropriately by the generator designer, 
       by generating builders, mills, etc. for each form of product - quite similar 
       to MontiCore itself.
       The generated code is usually structured along the components or sub-systems
       that the software architecture defines.
    5. The RTE is probably well-designed if it is usable in a normal framework.
* Please note: it is not easy to design modularity and extensibility from beginning.
  Framework design has shown that this is an iterative optimizing process.
  It must be avoided to design too many extension elements into the system
  from the beginning, because this adds a lot of complexity.
* Defined by: BR  

### Realizing Embedding through an Interface Nonterminal Extension Point

Consider the following scenario: 
A language `Host` defines an extension point through an interface nonterminal.

```
grammar Host { A = I*; interface I; }
```

Another language `Embedded`, that has no connection to the `Host` language, 
defines a class nonterminal `E`.

```
grammar Embedded { E = "something"; }
```

MontiCore provides alternative solutions to embed the language `Embedded`
into the language `Host` at the extension point `I`. All solutions presented here
require to implement a new grammar `G` that extends the grammars `Embedded` and `Host` 
reuses the start nonterminal of the `Host` grammar:

```
grammar G extends Host, Embedded { start A; }
```

The connection between extension point and extension is performed by an additional
grammar rule in the grammar `G`. This can be realized in one of the following ways each one
of which has its own advantages and disadvantages:

1. Embedding through overriding of extension rule and implementing extension point:
    * `E implements I;`
    * Advantage: simple embedding rule
    * Disadvantage: does not work in combination with inheritance of extension rule
    * Should therefore only be used, if `E` is not used anywhere else (= in not other language that is potentially used in combination with this language) 
2. Embedding through extending extension rule and implementing extension point rule:
    * `IE extends E implements I = "something";`
    * Advantage: does work in combination with inheritance of extension rule
    * Disadvantage: cloning of RHS of the extension rule can produce inconsistencies if `E` is changed
    * Can be used if it is assured that this rule is adjusted whenever `E` is changed, e.g., by assuming that `E` is not modified at all
3. Embedding through implementing extension point rule and providing extension on right-hand side:
    * `IE implements I = E;`
    * Advantage: does work in combination with inheritance of extension rule
    * Disadvantage: introduces new level of indirection in the AST that invalidates the check whether the required abstract syntax (RHS of interface nonterminal) is present
    * Should therefore not be used, if the interface has a right-hand side
* Defined by: AB


## **Recurring Language Components**


### The import statements

* Many models depend on other models from which they receive symbols they can rely on.
  To define this kind of dependencies using import statements is convenient and well 
  known (e.g., from Java). We thus suggest to use the import statement in the spirit of Java.
  * `import aName` at the first sight means that a specific class with the qualified
  name `aName` is used. In reality, however, Java has a very convenient convention
  that class `aName` is always defined in the artifact (i.e. file) with the same name 
  `aName.java` and the needed symbol table is part of `aName.class`. So an import 
  statement actually locates an artifact.
* As a consequence, we suggest:
    * `import aModelName` refers to an artifact with name `aModelName` -- regardless
      which kind of model is defined there.
    * All the symbols exported by the artifact `aModelName` are imported when using 
      the import statement `import aModelName`. 
    * The imported artifact provides the desired symbols, typically stored through 
      an earlier tool execution in a symbol file `aModelName.sym`.
    * The symbol file may have specific extensions, such as `autsym`or `cdsym`.
    * Selective import (known from Java), such as `import aName.innerClass` 
      should be possible, but currently no such showcase has been made yet (beyond Java).
    * The import statement is only used to make symbols available in their simple form.
      It is usually 
      not intended to explicate a single dependency, e.g., a configuration model
      that depends on exactly one base model. Like in Java, where you import an 
      artifact and then still explicitly extend the contained class.
* It is methodically of interest to store at most one artifact with the same
  qualified name (although it is not per se forbidden to have more). 
  Java then also uses the first occurring class in its classpath only.
* In a heterogeneous language setting, it may be necessary to map symbols
  from a source to a target form (e.g., state symbols to Java enum constants or state 
  pattern classes). There are three main options for this task:
    1. Store in the desired target symbol form upon creating the symbol file.
       Has some problems: (1) increases dependencies between tools, 
       (2) potentially several files need to be stored.
    2. Adapt the imported symbols upon loading (recommended).
    3. Use an explicit transformation tool between the two model processing tools
       to map the initially stored symbol file to the desired format.

### Version number in language variants

* As an important rule:
    * Do not include version numbers in the DSL explicitly.
* The reason is that whenever you do a tooling update, all the models that have 
  been defined before are suddenly not valid anymore and have to be adapted.
  Java has very carefully ensured that updates in the language are extensions only 
  and thus all old Java files are still validated with new Java compilers 
  (with the one exception: new keyword `assert`).
* If your language is still very volatile against disruptive changes, 
  it may be an option at the beginning, but should be avoided with the first real release.
* It is a burden to manage version numbers and downward compatibility through 
  all the versioning, especially if language components evolve with their own 
  versioning.
* MontiCore provides a theory of *conservative extension* to avoid
  explicit version controlling within the language.
* And if needed: MontiCore and their tools provide extensive checks of 
  wellformedness (i.e.
  context conditions), on each update a fully automated consistency check 
  of all existing models
  should be easily establishable.



## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

