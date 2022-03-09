package com.axonivy.ivy.process.element.rest.start.ui;

import java.util.Arrays;
import java.util.List;

import com.axonivy.ivy.process.element.rest.start.RestStartExtension;

import ch.ivyteam.ivy.designer.inscription.ui.masks.fw.IInscriptionEditorTab;
import ch.ivyteam.ivy.designer.inscription.ui.thirdparty.BpmnInscriptionEditor;
import ch.ivyteam.ivy.process.config.element.ElementConfigurator;
import ch.ivyteam.ivy.process.config.event.start.pi.ThirdPartyProgramStartConfigurator;
import ch.ivyteam.ivy.process.model.NodeElement;

@SuppressWarnings("all")
public class InscriptionEditorRestStart implements BpmnInscriptionEditor
{
  @Override
  public boolean isResponsible(String processElementIdentifier)
  {
    return RestStartExtension.REST_START.equals(processElementIdentifier);
  }

  @Override
  public <T extends ElementConfigurator<? extends NodeElement>> List<IInscriptionEditorTab> getTabs(T configurator)
  {
    return getTabs((ThirdPartyProgramStartConfigurator) configurator);
  }

  private List<IInscriptionEditorTab> getTabs(ThirdPartyProgramStartConfigurator configurator)
  {
    RestStartUiModel starttUiModel = new RestStartUiModel(configurator);
    RestResultUiModel resultUiModel = new RestResultUiModel(configurator);
    return Arrays.asList(new RestStartTab(starttUiModel), new RestResultTab(resultUiModel));
  }
}
