package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.core.Environment;
import freemarker.template.TemplateModelException;

import java.util.ArrayList;
import java.util.List;

public class DefineHookPointWithDefault3Alias extends GlexAlias {
  public DefineHookPointWithDefault3Alias() {
    super("defineHookPointWithDefault3", "defineHookPointWithDefault");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    exactArguments(arguments, 3);

    ArrayList args = new ArrayList(arguments);
    args.add(0, Environment.getCurrentEnvironment().getVariable("tc"));
    return super.exec(args);
  }
}
