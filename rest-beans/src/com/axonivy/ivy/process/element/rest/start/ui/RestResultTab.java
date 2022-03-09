package com.axonivy.ivy.process.element.rest.start.ui;

import org.eclipse.swt.widgets.Composite;

import ch.ivyteam.ivy.designer.inscription.ui.model.AbstractUiModelSwtInscriptionTab;
import ch.ivyteam.ivy.designer.inscription.ui.masks.fw.IInscriptionEditorTab;
import ch.ivyteam.ivy.ui.model.swt.IvySwtBinder;

public class RestResultTab extends AbstractUiModelSwtInscriptionTab<RestResultUiModel> implements IInscriptionEditorTab
{
  protected RestResultTab(RestResultUiModel model)
  {
    super(model);
  }

  @Override
  public String getTabName()
  {
    return "Result";
  }

  @Override
  protected Composite createUiAndBindToModel(Composite parent, IvySwtBinder ivySwtBinder)
  {
    RestResultComposite composite = new RestResultComposite(parent);
    ivySwtBinder.bind(model.outputParams).to(composite.outputParamsTable);
    ivySwtBinder.bind(model.dataToOutputParamMapping).to(composite.parameterMapping);
    composite.propertySelector.setExpanded(!model.outputParams.isDefault());
    return composite;
  }
}
