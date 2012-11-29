package com.sandwell.JavaSimulation3D;

import java.awt.Font;
import java.util.ArrayList;

import com.jaamsim.ui.View;
import com.sandwell.JavaSimulation.BooleanInput;
import com.sandwell.JavaSimulation.ColourInput;
import com.sandwell.JavaSimulation.EntityListInput;
import com.sandwell.JavaSimulation.Input;
import com.sandwell.JavaSimulation.IntegerInput;
import com.sandwell.JavaSimulation.IntegerListInput;
import com.sandwell.JavaSimulation.IntegerVector;
import com.sandwell.JavaSimulation.Keyword;
import com.sandwell.JavaSimulation.StringChoiceInput;
import com.sandwell.JavaSimulation.StringInput;
import com.sandwell.JavaSimulation.StringListInput;
import com.sandwell.JavaSimulation.StringVector;

public class OverlayTextLabel extends DisplayEntity {

	@Keyword(desc = "The static text to be displayed.  If spaces are included, enclose the text in single quotes.",
	         example = "TitleLabel Text { 'Example Simulation Model' }")
	private final StringInput text;

	@Keyword(desc = "The height of the font as displayed in the view window. Unit is in pixels.",
	         example = "TitleLabel TextHeight { 15 }")
	private final IntegerInput textHeight;

	@Keyword(desc = "The position of the label, from the upper left corner of the window to the upper left corner " +
	                "of the label. Value is in pixels",
	     example = "TitleLabel Position { 20 20}")
	private final IntegerListInput screenPosition;

	@Keyword(desc = "The name of the font to be used for the label. The " +
	                "font name must be enclosed in single quotes.",
	         example = "TitleLabel FontName { 'Arial' }")
	private final StringChoiceInput fontName;

	@Keyword(desc = "A list of font styles to be applied to the label, e.g. Bold, Italic. ",
	         example = "TitleLabel FontStyle { Bold }  ")
	private final StringListInput fontStyle;

	@Keyword(desc = "The colour of the font, defined using a colour keyword or RGB values.",
	         example = "TitleLabel FontColor { Red }")
	private final ColourInput fontColor;

	@Keyword(desc = "The view objects this overlay string will be visible on",
	         example = "TitleLabel VisibleViews { TitleView DefaultView }")
	private final EntityListInput<View> visibleViews;

	@Keyword(desc = "If this text label should be aligned from the right edge of the window (instead of the left)",
	         example = "TitleLabel AlignRight { TRUE }")
	private final BooleanInput alignRight;

	@Keyword(desc = "If this text label should be aligned from the bottom edge of the window (instead of the top)",
	         example = "TitleLabel AlignBottom { TRUE }")
	private final BooleanInput alignBottom;

	private int style = 0;

	{
		text = new StringInput("Text", "Key Inputs", "abc");
		this.addInput(text, true, "Label");

		textHeight = new IntegerInput("TextHeight", "Key Inputs", 15);
		textHeight.setValidRange(0, 1000);
		this.addInput(textHeight, true);

		IntegerVector defPos = new IntegerVector(2);
		defPos.add(10);
		defPos.add(10);
		screenPosition = new IntegerListInput("ScreenPosition", "Key Inputs", defPos);
		screenPosition.setValidCount(2);
		screenPosition.setValidRange(0, 2500);
		this.addInput(screenPosition, true);

		fontName = new StringChoiceInput("FontName", "Key Inputs", TextLabel.defFont);
		fontName.setChoices(TextLabel.validFontNames);
		this.addInput(fontName, true);

		fontColor = new ColourInput("FontColour", "Key Inputs", ColourInput.BLACK);
		this.addInput(fontColor, true, "FontColor");

		fontStyle = new StringListInput("FontStyle", "Key Inputs", new StringVector());
		fontStyle.setValidOptions(TextLabel.validStyles);
		fontStyle.setCaseSensitive(false);
		this.addInput(fontStyle, true);

		visibleViews = new EntityListInput<View>(View.class, "VisibleViews", "Key Inputs", new ArrayList<View>(0));
		this.addInput(visibleViews, true);

		alignRight = new BooleanInput("AlignRight", "Key Inputs", false);
		this.addInput(alignRight, true);

		alignBottom = new BooleanInput("AlignBottom", "Key Inputs", false);
		this.addInput(alignBottom, true);

		getInput("position").setHidden(true);
		getInput("alignment").setHidden(true);
		getInput("size").setHidden(true);
		getInput("orientation").setHidden(true);
		getInput("region").setHidden(true);
		getInput("relativeentity").setHidden(true);
		getInput("levelofdetail").setHidden(true);
		getInput("displaymodel").setHidden(true);
		getInput("active").setHidden(true);
		getInput("show").setHidden(true);
		getInput("movable").setHidden(true);
		getInput("tooltip").setHidden(true);

	}

	public int getFontStyle() {
		return style;
	}

	public String getFontName() {
		return fontName.getChoice();
	}

	@Override
	public void updateForInput( Input<?> in ) {
		if(in == fontStyle) {
			style = Font.PLAIN;
			for(String each: fontStyle.getValue() ) {
				if(each.equalsIgnoreCase("Bold") ) {
					style += Font.BOLD;
				}
				else if (each.equalsIgnoreCase("Italic")) {
					style += Font.ITALIC;
				}
			}
		}
		setGraphicsDataDirty();
	}

	public ArrayList<View> getVisibleViews() {

		if( visibleViews.getValue() == null )
			return new ArrayList<View>();

		return visibleViews.getValue();
	}


}