/*
 * Copyright 2023 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.easynpc.client.screen.configuration.dialog;

import com.mojang.blaze3d.vertex.PoseStack;
import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.client.screen.components.CancelButton;
import de.markusbordihn.easynpc.client.screen.components.SaveButton;
import de.markusbordihn.easynpc.client.screen.components.Text;
import de.markusbordihn.easynpc.client.screen.components.TextButton;
import de.markusbordihn.easynpc.client.screen.components.TextField;
import de.markusbordihn.easynpc.data.configuration.ConfigurationType;
import de.markusbordihn.easynpc.data.dialog.DialogButtonEntry;
import de.markusbordihn.easynpc.data.dialog.DialogDataEntry;
import de.markusbordihn.easynpc.data.dialog.DialogDataSet;
import de.markusbordihn.easynpc.data.dialog.DialogType;
import de.markusbordihn.easynpc.data.dialog.DialogUtils;
import de.markusbordihn.easynpc.menu.configuration.ConfigurationMenu;
import de.markusbordihn.easynpc.network.NetworkMessageHandlerManager;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;

public class YesNoDialogConfigurationScreen<T extends ConfigurationMenu>
    extends DialogConfigurationScreen<T> {

  private static final String YES_BUTTON_LABEL = "yes_button";
  private static final String NO_BUTTON_LABEL = "no_button";
  protected EditBox mainDialogBox;
  protected Button yesDialogButton;
  protected Button noDialogButton;
  protected EditBox yesDialogBox;
  protected EditBox noDialogBox;
  protected Button saveButton = null;
  protected Button cancelButton = null;
  boolean showSaveNotificationForButtons = false;
  private String noDialogValue = "";
  private String questionDialogValue = "";
  private String yesDialogValue = "";

  public YesNoDialogConfigurationScreen(T menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
  }

  @Override
  public void init() {
    super.init();

    // Default button stats
    this.yesNoDialogButton.active = false;

    // Dialog Data
    DialogDataEntry questionDialogData = this.getDialogDataSet().getDialog("question");
    DialogDataEntry yesDialogData = this.getDialogDataSet().getDialog("yes_answer");
    DialogDataEntry noDialogData = this.getDialogDataSet().getDialog("no_answer");

    // Dialog Buttons
    DialogButtonEntry yesButtonData =
        questionDialogData == null ? null : questionDialogData.getDialogButton(YES_BUTTON_LABEL);
    DialogButtonEntry noButtonData =
        questionDialogData == null ? null : questionDialogData.getDialogButton(NO_BUTTON_LABEL);

    // Question Text (copy from basic text if not set)
    this.questionDialogValue = questionDialogData == null ? "" : questionDialogData.getText();
    if (this.questionDialogValue.isEmpty()
        && this.getDialogDataSet().getDefaultDialog() != null
        && this.getDialogDataSet().getType() == DialogType.BASIC) {
      this.questionDialogValue = this.getDialogDataSet().getDefaultDialog().getText();
    }

    // Save notification for buttons
    if (questionDialogData == null) {
      this.showSaveNotificationForButtons = true;
    }

    // Dialog
    this.mainDialogBox = new TextField(this.font, this.contentLeftPos, this.topPos + 50, 300);
    this.mainDialogBox.setMaxLength(512);
    this.mainDialogBox.setValue(this.questionDialogValue);
    this.addRenderableWidget(this.mainDialogBox);

    // Question Dialog Buttons
    this.yesDialogButton =
        this.addRenderableWidget(
            new TextButton(
                this.contentLeftPos,
                this.topPos + 85,
                145,
                yesButtonData == null
                    ? new TextComponent("Yes Button")
                    : yesButtonData.getButtonName(20),
                onPress -> {
                  UUID yesButtonId = yesButtonData == null ? null : yesButtonData.id();
                  if (questionDialogData != null) {
                    if (yesButtonId != null) {
                      NetworkMessageHandlerManager.getServerHandler()
                          .openDialogButtonEditor(
                              getEasyNPCUUID(), questionDialogData.getId(), yesButtonId);
                    } else {
                      NetworkMessageHandlerManager.getServerHandler()
                          .openDialogButtonEditor(getEasyNPCUUID(), questionDialogData.getId());
                    }
                  }
                }));

    this.noDialogButton =
        this.addRenderableWidget(
            new TextButton(
                this.contentLeftPos + 155,
                this.topPos + 85,
                145,
                noButtonData == null
                    ? new TextComponent("No Button")
                    : noButtonData.getButtonName(20),
                onPress -> {
                  UUID noButtonId = noButtonData == null ? null : noButtonData.id();
                  if (questionDialogData != null) {
                    if (noButtonId != null) {
                      NetworkMessageHandlerManager.getServerHandler()
                          .openDialogButtonEditor(
                              getEasyNPCUUID(), questionDialogData.getId(), noButtonId);
                    } else {
                      NetworkMessageHandlerManager.getServerHandler()
                          .openDialogButtonEditor(getEasyNPCUUID(), questionDialogData.getId());
                    }
                  }
                }));

    // Yes Dialog
    this.yesDialogValue = yesDialogData == null ? "" : yesDialogData.getText();
    this.yesDialogBox = new TextField(this.font, this.contentLeftPos, this.topPos + 130, 300);
    this.yesDialogBox.setMaxLength(255);
    this.yesDialogBox.setValue(this.yesDialogValue);
    this.addRenderableWidget(this.yesDialogBox);

    // No Dialog
    this.noDialogValue = noDialogData == null ? "" : noDialogData.getText();
    this.noDialogBox = new TextField(this.font, this.contentLeftPos, this.topPos + 170, 300);
    this.noDialogBox.setMaxLength(255);
    this.noDialogBox.setValue(this.noDialogValue);
    this.addRenderableWidget(this.noDialogBox);

    // Save Button
    this.saveButton =
        this.addRenderableWidget(
            new SaveButton(
                this.contentLeftPos + 26,
                this.bottomPos - 40,
                "save",
                onPress -> {
                  DialogDataSet dialogDataSet =
                      DialogUtils.getYesNoDialog(
                          this.mainDialogBox.getValue(),
                          "Yes",
                          "No",
                          this.yesDialogBox.getValue(),
                          this.noDialogBox.getValue());

                  // Check if we have a question dialog and add yes/no buttons if not available.
                  if (questionDialogData != null) {
                    Set<DialogButtonEntry> dialogButtonEntrySet =
                        questionDialogData.getDialogButtons();

                    // Double-check that we have valid yes_no buttons, in the case the user
                    // renamed the labels to something else and not to get lost.
                    if (!questionDialogData.hasDialogButton(YES_BUTTON_LABEL)) {
                      dialogButtonEntrySet.add(
                          dialogDataSet.getDialog("question").getDialogButton(YES_BUTTON_LABEL));
                    }
                    if (!questionDialogData.hasDialogButton(NO_BUTTON_LABEL)) {
                      dialogButtonEntrySet.add(
                          dialogDataSet.getDialog("question").getDialogButton(NO_BUTTON_LABEL));
                    }

                    // Update dialog buttons for question dialog
                    dialogDataSet.getDialog("question").setDialogButtons(dialogButtonEntrySet);
                  }

                  this.questionDialogValue = this.mainDialogBox.getValue();
                  this.yesDialogValue = this.yesDialogBox.getValue();
                  this.noDialogValue = this.noDialogBox.getValue();
                  NetworkMessageHandlerManager.getServerHandler()
                      .saveDialogSet(getEasyNPCUUID(), dialogDataSet);
                  NetworkMessageHandlerManager.getServerHandler()
                      .openConfiguration(getEasyNPCUUID(), ConfigurationType.YES_NO_DIALOG);
                }));

    // Chancel Button
    this.cancelButton =
        this.addRenderableWidget(
            new CancelButton(
                this.rightPos - 130,
                this.bottomPos - 40,
                "cancel",
                onPress -> this.showMainScreen()));
  }

  @Override
  public void render(PoseStack poseStack, int x, int y, float partialTicks) {
    super.render(poseStack, x, y, partialTicks);

    // Edit box Labels
    Text.drawConfigString(poseStack, this.font, "question", this.contentLeftPos, this.topPos + 40);

    Text.drawConfigString(
        poseStack, this.font, "edit_yes_button", this.contentLeftPos, this.topPos + 75);

    Text.drawConfigString(
        poseStack, this.font, "edit_no_button", this.contentLeftPos + 155, this.topPos + 75);

    if (this.yesDialogBox != null) {
      Text.drawConfigString(
          poseStack, this.font, "yes_answer", this.contentLeftPos, this.yesDialogBox.y - 12);
    }

    if (this.noDialogBox != null) {
      Text.drawConfigString(
          poseStack, this.font, "no_answer", this.contentLeftPos, this.noDialogBox.y - 12);
    }

    // Save notification
    if (this.showSaveNotificationForButtons) {
      Text.drawConfigString(
          poseStack,
          this.font,
          "save_before_edit_buttons",
          this.contentLeftPos + 65,
          this.topPos + 105,
          Constants.FONT_COLOR_RED);
    }
  }

  @Override
  public void updateTick() {
    super.updateTick();

    if (this.saveButton != null) {
      this.saveButton.active =
          !this.mainDialogBox.getValue().equals(this.questionDialogValue)
              || !this.yesDialogBox.getValue().equals(this.yesDialogValue)
              || !this.noDialogBox.getValue().equals(this.noDialogValue);
    }

    if (yesDialogButton != null && this.getDialogDataSet() != null) {
      yesDialogButton.active =
          this.getDialogDataSet().hasDialog("question")
              && this.getDialogDataSet().getDialog("question").hasDialogButton(YES_BUTTON_LABEL);
    }

    if (noDialogButton != null && this.getDialogDataSet() != null) {
      noDialogButton.active =
          this.getDialogDataSet().hasDialog("question")
              && this.getDialogDataSet().getDialog("question").hasDialogButton(NO_BUTTON_LABEL);
    }
  }
}
