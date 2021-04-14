<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarname", "startprod")}


if (args.length != 1) {
      Log.error("Please specify only one single path to the input model.");
      return;
    }
    String model = args[0];

// setup the deser infrastructure
    final ${grammarname}ScopeDeSer deser = new ${grammarname}ScopeDeSer();

// parse the model and create the AST representation
       final ${startprod} ast = parse(model);
        Log.info(model + " parsed successfully!", StatechartRunner.class.getName());

 // setup the symbol table
     I${grammarname}ArtifactScope modelTopScope = createSymbolTable(ast);

     // can be used for resolving things in the model
     Optional<StateSymbol> aSymbol = modelTopScope.resolveState("Ping");
     if (aSymbol.isPresent()) {
       Log.info("Resolved state symbol \"Ping\"; FQN = " + aSymbol.get().toString(),
           StatechartRunner.class.getName()); <---
     }

     // execute default context conditions
     runDefaultCoCos(ast);

     // store artifact scope
     String qualifiedModelName = model.replace("src/main/resources", "");
     qualifiedModelName = qualifiedModelName.replace("src/test/resources", "");
     String outputFileName = Paths.get(model).getFileName()+"sym";
     String packagePath = Paths.get(qualifiedModelName).getParent().toString();
     String storagePath = Paths.get("target/symbols", packagePath,
         outputFileName).toString();
     deser.store(modelTopScope,storagePath);

     // analyze the model with a visitor
     CountStates cs = new CountStates();
     cs.handle(ast);
     Log.info("The model contains " + cs.getCount() + " states.", StatechartRunner.class.getName()); <---

     // execute a pretty printer
     PrettyPrinter pp = new PrettyPrinter();
     pp.handle(ast);
     Log.info("Pretty printing the parsed Cli into console:", RunnerDecorator.class.getName());
     System.out.println(pp.getResult());
   }