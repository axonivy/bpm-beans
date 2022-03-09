package com.axonivy.ivy.process.element.blockchain.ui;

import org.eclipse.swt.widgets.Composite;

import ch.ivyteam.ivy.designer.inscription.ui.masks.fw.IInscriptionEditorTab;
import ch.ivyteam.ivy.designer.inscription.ui.model.AbstractUiModelSwtInscriptionTab;
import ch.ivyteam.ivy.ui.model.swt.IvySwtBinder;

public class BlockchainRequestTab extends AbstractUiModelSwtInscriptionTab<BlockchainRequestUiModel> implements IInscriptionEditorTab {

  public BlockchainRequestTab(BlockchainRequestUiModel model)
  {
    super(model);
  }

  @Override
  public String getTabName()
  {
    return "Request";
  }

  BlockchainRequestUiModel getRequestTabUiModel()
  {
    return model;
  }

  @Override
  protected Composite createUiAndBindToModel(Composite parent, IvySwtBinder ivySwtBinder)
  {
    BlockchainRequestComposite composite = new BlockchainRequestComposite(parent);
    ivySwtBinder.bind(model.contracts).to(composite.contractCombo);
    ivySwtBinder.bind(model.functions).to(composite.functionCombo);
    ivySwtBinder.bind(model.properties).to(composite.propertiesTable);
    ivySwtBinder.bind(model.parameters).to(composite.parameterMapping);
    composite.propertySelector.setExpanded(!model.properties.isDefault());
    return composite;
  }
}
