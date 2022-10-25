package com.axonivy.ivy.process.element.blockchain.ui;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.axonivy.ivy.process.element.blockchain.EthereumProcessElement;

import ch.ivyteam.ivy.designer.inscription.ui.masks.fw.IInscriptionEditorTab;
import ch.ivyteam.ivy.designer.process.ui.info.IBpmnProcessElementUi;
import ch.ivyteam.ivy.process.config.activity.pi.ThirdPartyProgramInterfaceConfigurator;
import ch.ivyteam.ivy.process.config.element.ElementConfigurator;
import ch.ivyteam.ivy.process.model.NodeElement;

public class EthereumProcessElementUi implements IBpmnProcessElementUi
{
  @Override
  public String getName()
  {
    return EthereumProcessElement.ETHEREUM_ACTIVITY;
  }

  @Override
  public URL getIcon()
  {
    return getClass().getResource("EthereumActivity.png");
  }

  @Override
  public String getHelpPath()
  {
    return "https://github.com/ivy-supplements/bpm-beans/blob/master/blockchain-beans/README.md";
  }

  @Override
  public List<IInscriptionEditorTab> getEditorTabs(ElementConfigurator<? extends NodeElement> configurator)
  {
    return getThirdpartyTabs((ThirdPartyProgramInterfaceConfigurator)configurator);
  }

  private List<IInscriptionEditorTab> getThirdpartyTabs(ThirdPartyProgramInterfaceConfigurator configurator)
  {
    BlockchainRequestUiModel requestUiModel = new BlockchainRequestUiModel(configurator);
    BlockchainResponseUiModel responseUiModel = new BlockchainResponseUiModel(configurator, requestUiModel);
    return Arrays.asList(new BlockchainRequestTab(requestUiModel), new BlockchainResponseTab(responseUiModel));
  }
}
