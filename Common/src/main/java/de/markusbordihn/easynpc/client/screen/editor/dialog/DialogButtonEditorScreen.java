/*
 * Copyright 2023 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.easynpc.client.screen.editor.dialog;

import com.mojang.blaze3d.vertex.PoseStack;
import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.client.screen.EditorScreen;
import de.markusbordihn.easynpc.client.screen.components.AddButton;
import de.markusbordihn.easynpc.client.screen.components.CancelButton;
import de.markusbordihn.easynpc.client.screen.components.Checkbox;
import de.markusbordihn.easynpc.client.screen.components.DeleteButton;
import de.markusbordihn.easynpc.client.screen.components.DialogButton;
import de.markusbordihn.easynpc.client.screen.components.DialogButtonButton;
import de.markusbordihn.easynpc.client.screen.components.EditButton;
import de.markusbordihn.easynpc.client.screen.components.SaveButton;
import de.markusbordihn.easynpc.client.screen.components.SpriteButton;
import de.markusbordihn.easynpc.client.screen.components.Text;
import de.markusbordihn.easynpc.client.screen.components.TextButton;
import de.markusbordihn.easynpc.client.screen.components.TextField;
import de.markusbordihn.easynpc.data.action.ActionDataEntry;
import de.markusbordihn.easynpc.data.action.ActionDataSet;
import de.markusbordihn.easynpc.data.configuration.ConfigurationType;
import de.markusbordihn.easynpc.data.dialog.DialogButtonEntry;
import de.markusbordihn.easynpc.data.dialog.DialogUtils;
import de.markusbordihn.easynpc.data.editor.EditorType;
import de.markusbordihn.easynpc.menu.editor.EditorMenu;
import de.markusbordihn.easynpc.network.NetworkMessageHandlerManager;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;

public class DialogButtonEditorScreen<T extends EditorMenu> extends EditorScreen<T> {

  protected Button homeButton;
  protected Button dialogButton;
  protected Button dialogButtonButton;
  protected Button saveButton;
  protected Button cancelButton;
  protected Button deleteButton;
  protected TextField buttonNameBox;
  protected Button buttonNameToLabelButton;
  protected TextField buttonLabelBox;
  protected Checkbox buttonLabelCheckbox;
  private String buttonLabelValue = "";
  private String buttonNameValue = "";

  public DialogButtonEditorScreen(T menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
  }

  private void deleteDialogButton() {
    if (this.minecraft == null) {
      return;
    }
    this.minecraft.setScreen(
        new ConfirmScreen(
            confirmed -> {
              if (confirmed && getDialogUUID() != null) {
                NetworkMessageHandlerManager.getServerHandler()
                    .removeDialogButton(getEasyNPCUUID(), getDialogUUID(), getDialogButtonUUID());
                NetworkMessageHandlerManager.getServerHandler()
                    .openDialogEditor(this.getEasyNPCUUID(), this.getDialogUUID());
              } else {
                this.minecraft.setScreen(this);
              }
            },
            new TranslatableComponent(Constants.TEXT_PREFIX + "removeDialogButton.deleteQuestion"),
            new TranslatableComponent(
                Constants.TEXT_PREFIX + "removeDialogButton.deleteWarning",
                this.getDialogButtonData().name()),
            new TranslatableComponent(Constants.TEXT_PREFIX + "removeDialogButton.deleteButton"),
            CommonComponents.GUI_CANCEL));
  }

  protected void renderEditLabels(PoseStack poseStack) {

    if (this.buttonNameBox != null) {
      Text.drawConfigString(
          poseStack, this.font, "button.name", leftPos + 12, this.buttonNameBox.y + 4);
    }

    if (this.buttonLabelBox != null) {
      Text.drawConfigString(
          poseStack, this.font, "label_id", leftPos + 12, this.buttonLabelBox.y + 4);
    }
  }

  @Override
  public void init() {
    super.init();

    // Home Button
    this.homeButton =
        this.addRenderableWidget(
            new TextButton(
                this.leftPos + 7,
                this.topPos + 7,
                10,
                18,
                "<",
                onPress ->
                    NetworkMessageHandlerManager.getServerHandler()
                        .openConfiguration(this.getEasyNPCUUID(), ConfigurationType.DIALOG)));

    // Dialog Button
    this.dialogButton =
        this.addRenderableWidget(
            new DialogButton(
                this.homeButton.x + this.homeButton.getWidth(),
                this.topPos + 7,
                140,
                this.getDialogData().getName(21),
                onPress ->
                    NetworkMessageHandlerManager.getServerHandler()
                        .openDialogEditor(this.getEasyNPCUUID(), this.getDialogUUID())));

    // Dialog Button Button
    this.dialogButtonButton =
        this.addRenderableWidget(
            new DialogButtonButton(
                this.dialogButton.x + this.dialogButton.getWidth(),
                this.topPos + 7,
                140,
                this.getDialogButtonData().getButtonName(21).getString(),
                onPress -> {}));
    this.dialogButtonButton.active = false;

    // Button Name
    this.buttonNameValue = this.getDialogButtonData().name();
    this.buttonNameBox = new TextField(this.font, this.leftPos + 100, this.topPos + 30, 150);
    this.buttonNameBox.setMaxLength(64);
    this.buttonNameBox.setValue(this.buttonNameValue);
    this.addRenderableWidget(this.buttonNameBox);

    // Convert Button Name to Button Label
    this.buttonNameToLabelButton =
        this.addRenderableWidget(
            new SpriteButton(
                this.buttonNameBox.x + this.buttonNameBox.getWidth() + 1,
                this.buttonNameBox.y - 1,
                18,
                18,
                4,
                4,
                80,
                80,
                12,
                12,
                onPress -> {
                  if (this.buttonNameBox != null && this.buttonLabelBox != null) {
                    String buttonName = this.buttonNameBox.getValue();
                    this.buttonLabelBox.setValue(DialogUtils.generateButtonLabel(buttonName));
                  }
                }));

    // Button Label
    this.buttonLabelValue = this.getDialogButtonData().label();
    this.buttonLabelBox = new TextField(this.font, this.leftPos + 100, this.topPos + 50, 100);
    this.buttonLabelBox.setMaxLength(DialogButtonEntry.MAX_BUTTON_LABEL_LENGTH);
    this.buttonLabelBox.setValue(this.buttonLabelValue);
    this.buttonLabelBox.setEditable(false);
    this.addRenderableWidget(this.buttonLabelBox);

    // Lock Button Label by default to prevent accidental changes.
    this.buttonLabelCheckbox =
        this.addRenderableWidget(
            new Checkbox(
                this.leftPos + 204,
                this.buttonLabelBox.y + 1,
                "locked",
                true,
                onPress -> this.buttonLabelBox.setEditable(!this.buttonLabelCheckbox.selected())));

    // Dialog Button Action Data
    this.addRenderableWidget(this.getActionDataButton(this.leftPos + 10, this.topPos + 70));

    // Save Button
    this.saveButton =
        this.addRenderableWidget(
            new SaveButton(
                this.leftPos + 25,
                this.bottomPos - 35,
                85,
                "save",
                onPress -> {
                  this.saveDialogButton();
                  NetworkMessageHandlerManager.getServerHandler()
                      .openDialogEditor(this.getEasyNPCUUID(), this.getDialogUUID());
                }));

    // Delete Button
    this.deleteButton =
        this.addRenderableWidget(
            new DeleteButton(
                this.saveButton.x + this.saveButton.getWidth() + 10,
                this.bottomPos - 35,
                85,
                onPress -> this.deleteDialogButton()));

    // Chancel Button
    this.cancelButton =
        this.addRenderableWidget(
            new CancelButton(
                this.deleteButton.x + this.deleteButton.getWidth() + 10,
                this.bottomPos - 35,
                85,
                "cancel",
                onPress ->
                    NetworkMessageHandlerManager.getServerHandler()
                        .openDialogEditor(this.getEasyNPCUUID(), this.getDialogUUID())));
  }

  private void saveDialogButton() {
    DialogButtonEntry dialogButtonEntry = this.getDialogButtonData();
    if (dialogButtonEntry == null) {
      return;
    }

    // Basic dialog button data
    DialogButtonEntry newDialogButtonEntry =
        dialogButtonEntry
            .withName(this.buttonNameBox.getValue())
            .withLabel(this.buttonLabelBox.getValue());

    // Save dialog button action data.
    NetworkMessageHandlerManager.getServerHandler()
        .saveDialogButton(
            this.getEasyNPCUUID(),
            this.getDialogUUID(),
            this.getDialogButtonUUID(),
            newDialogButtonEntry);
  }

  @Override
  public void updateTick() {
    super.updateTick();

    if (this.saveButton != null) {
      this.saveButton.active =
          !this.buttonNameBox.getValue().equals(this.buttonNameValue)
              || !this.buttonLabelBox.getValue().equals(this.buttonLabelValue);
    }

    if (this.buttonLabelCheckbox != null && this.buttonNameToLabelButton != null) {
      this.buttonNameToLabelButton.active = !this.buttonLabelCheckbox.selected();
    }
  }

  @Override
  public void render(PoseStack poseStack, int x, int y, float partialTicks) {
    this.renderBackground(poseStack);
    super.render(poseStack, x, y, partialTicks);
    this.renderEditLabels(poseStack);

    // Render Tooltips
    if (this.buttonNameToLabelButton != null && this.buttonNameToLabelButton.isMouseOver(x, y)) {
      this.renderTooltip(
          poseStack,
          new TranslatableComponent(Constants.TEXT_CONFIG_PREFIX + "name_to_label.tooltip"),
          x,
          y);
    }
  }

  protected Button getActionDataButton(int left, int top) {
    int buttonWidth = 300;
    ActionDataSet actionDataSet = this.getDialogButtonData().actionDataSet();
    if (actionDataSet == null || actionDataSet.isEmpty()) {
      Component buttonLabel =
          new TranslatableComponent(
              Constants.TEXT_CONFIG_PREFIX + "add_action", new TextComponent("button"));
      return new AddButton(
              left,
              top,
              buttonWidth,
              buttonLabel,
              onPress ->
                  NetworkMessageHandlerManager.getServerHandler()
                      .openActionDataEntryEditor(
                          this.getEasyNPCUUID(),
                          EditorType.DIALOG_BUTTON,
                          this.getDialogUUID(),
                          this.getDialogButtonUUID(),
                          new ActionDataEntry()))
          .setRenderCenter(false);
    } else {
      Component buttonLabel =
          new TranslatableComponent(
              Constants.TEXT_CONFIG_PREFIX + "edit_action", new TextComponent("button"));
      return new EditButton(
              left,
              top,
              buttonWidth,
              buttonLabel,
              onPress ->
                  NetworkMessageHandlerManager.getServerHandler()
                      .openActionDataEditor(
                          this.getEasyNPCUUID(),
                          EditorType.DIALOG_BUTTON,
                          this.getDialogUUID(),
                          this.getDialogButtonUUID()))
          .setRenderCenter(false);
    }
  }
}
