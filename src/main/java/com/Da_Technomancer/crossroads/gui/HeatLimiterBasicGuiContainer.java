package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDoubleToServer;
import com.Da_Technomancer.crossroads.API.templates.ButtonGuiObject;
import com.Da_Technomancer.crossroads.API.templates.TextBarGuiObject;
import com.Da_Technomancer.crossroads.API.templates.ToggleButtonGuiObject;
import com.Da_Technomancer.crossroads.gui.container.BlankContainer;
import com.Da_Technomancer.crossroads.tileentities.alchemy.HeatLimiterBasicTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;

import java.io.IOException;

public class HeatLimiterBasicGuiContainer extends GuiContainer{

	private final HeatLimiterBasicTileEntity te;
	private TextBarGuiObject textBar;
	private ButtonGuiObject clearButton;
	private ToggleButtonGuiObject multButton;
	private ToggleButtonGuiObject divButton;
	private ButtonGuiObject piButton;
	private ButtonGuiObject eulerButton;

	public HeatLimiterBasicGuiContainer(HeatLimiterBasicTileEntity te){
		super(new BlankContainer());
		xSize = 300;
		ySize = 20;
		this.te = te;
	}

	@Override
	public void initGui(){
		super.initGui();

		textBar = new TextBarGuiObject((width - xSize) / 2, (height - ySize) / 2, 0, 0, 300, 25, null, (Character key) -> key == '.' || Character.isDigit(key));
		textBar.setText(doubleToString(te.getSetting()));
		clearButton = new ButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 0, 20, 20, "C");
		multButton = new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 20, 20, 20, "⨉");
		divButton = new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 40, 20, 20, "÷");
		piButton = new ButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 60, 20, 20, "π");
		eulerButton = new ButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 80, 20, 20, "e");
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	private static String doubleToString(double d){
		String out = Double.toString(d);
		if(out.endsWith(".0")){
			out = out.substring(0, out.length() - 2);
		}
		return out;
	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		setOutput();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		textBar.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		clearButton.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		multButton.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		divButton.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		piButton.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		eulerButton.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		textBar.drawFore(mouseX, mouseY, fontRenderer);
		clearButton.drawFore(mouseX, mouseY, fontRenderer);
		multButton.drawFore(mouseX, mouseY, fontRenderer);
		divButton.drawFore(mouseX, mouseY, fontRenderer);
		piButton.drawFore(mouseX, mouseY, fontRenderer);
		eulerButton.drawFore(mouseX, mouseY, fontRenderer);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		if(!textBar.mouseClicked(x, y, button)){
			if(clearButton.mouseClicked(x, y, button)){
				textBar.setText("0");
			}else if(multButton.mouseClicked(x, y, button)){
				if(multButton.isDepressed()){
					if(divButton.isDepressed()){
						divButton.setDepressed(false);
						textBar.setText(Double.toString(prevValue));
					}
					try{
						prevValue = Double.parseDouble(textBar.getText());
					}catch(NumberFormatException e){
						multButton.setDepressed(false);
					}
					textBar.setText("");
				}else{
					try{
						double value = Double.parseDouble(textBar.getText());
						if(!Double.isFinite(value)){
							textBar.setText(Double.toString(prevValue));
						}else{
							value *= prevValue;
							textBar.setText(Double.toString(value));
						}
					}catch(NumberFormatException e){
						textBar.setText(Double.toString(prevValue));
					}

				}
			}else if(divButton.mouseClicked(x, y, button)){
				if(divButton.isDepressed()){
					if(multButton.isDepressed()){
						multButton.setDepressed(false);
						textBar.setText(Double.toString(prevValue));
					}
					try{
						prevValue = Double.parseDouble(textBar.getText());
					}catch(NumberFormatException e){
						divButton.setDepressed(false);
					}
					textBar.setText("");
				}else{
					try{
						double value = Double.parseDouble(textBar.getText());
						if(Math.abs(value) == 0 || !Double.isFinite(value)){
							textBar.setText(Double.toString(prevValue));
						}else{
							value = prevValue / value;
							textBar.setText(Double.toString(value));
						}
					}catch(NumberFormatException e){
						textBar.setText(Double.toString(prevValue));
					}

				}
			}else if(piButton.mouseClicked(x, y, button)){
				textBar.setText(Double.toString(Math.PI));
			}else if(eulerButton.mouseClicked(x, y, button)){
				textBar.setText(Double.toString(Math.E));
			}
		}
	}

	private double prevValue = 0;

	@Override
	protected void keyTyped(char key, int keyCode) throws IOException{
		if(!textBar.buttonPress(key, keyCode) && !clearButton.buttonPress(key, keyCode) && !multButton.buttonPress(key, keyCode) && !divButton.buttonPress(key, keyCode) && !piButton.buttonPress(key, keyCode) && !eulerButton.buttonPress(key, keyCode)){
			super.keyTyped(key, keyCode);
		}
	}

	private void setOutput(){
		double out = 0;
		boolean changed = false;
		try{
			out = Double.parseDouble(textBar.getText());
			if(Double.isFinite(out)){
				changed = true;
			}
		}catch(NumberFormatException e){
			changed = false;
		}
		out = Math.abs(out);
		if(changed && out != te.getSetting()){
			te.set(out);
			ModPackets.network.sendToServer(new SendDoubleToServer("new_setting", out, te.getPos(), te.getWorld().provider.getDimension()));
		}
	}
}