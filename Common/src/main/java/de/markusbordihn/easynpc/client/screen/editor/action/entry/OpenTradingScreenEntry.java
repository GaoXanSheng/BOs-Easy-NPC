package de.markusbordihn.easynpc.client.screen.editor.action.entry;

import com.mojang.blaze3d.vertex.PoseStack;
import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.client.screen.components.Text;
import de.markusbordihn.easynpc.client.screen.editor.action.ActionDataEntryEditorContainerScreen;
import de.markusbordihn.easynpc.data.action.ActionDataEntry;
import de.markusbordihn.easynpc.data.action.ActionDataSet;
import de.markusbordihn.easynpc.data.action.ActionDataType;

public class OpenTradingScreenEntry extends ActionEntryWidget {

  public OpenTradingScreenEntry(
      ActionDataEntry actionDataEntry,
      ActionDataSet actionDataSet,
      ActionDataEntryEditorContainerScreen<?> screen) {
    super(actionDataEntry, actionDataSet, screen);
  }

  @Override
  public void init(int editorLeft, int editorTop) {
    // No additional fields required for this action
  }

  @Override
  public void render(PoseStack poseStack, int editorLeft, int editorTop) {
    Text.drawString(
        poseStack,
        this.font,
        "Open Trading screen",
        editorLeft + 2,
        editorTop + 5,
        Constants.FONT_COLOR_DEFAULT);
  }

  @Override
  public ActionDataEntry getActionDataEntry() {
    return new ActionDataEntry(ActionDataType.OPEN_TRADING_SCREEN);
  }

  @Override
  public boolean hasChanged() {
    return !hasActionData(ActionDataType.OPEN_TRADING_SCREEN);
  }
}
