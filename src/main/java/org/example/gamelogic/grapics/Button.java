package org.example.gamelogic.grapics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.gamelogic.I_InputProvider;

public class Button {
    private double x;
    private double y;
    private double width;
    private double height;
    private String text;
    private Font font;
    private boolean isHovered;
    private boolean isClicked;
    
    // Colors
    private Color backgroundColor;
    private Color hoverBackgroundColor;
    private Color strokeColor;
    private Color hoverStrokeColor;
    private Color textColor;
    
    public Button(double x, double y, double width, double height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.font = new Font("Arial", 20);
        this.isHovered = false;
        this.isClicked = false;
        
        // Default colors
        this.backgroundColor = Color.color(0.13, 0.13, 0.13, 0.5);
        this.hoverBackgroundColor = Color.color(0.2, 0.2, 0.2, 0.7);
        this.strokeColor = Color.color(1, 1, 1, 0.8);
        this.hoverStrokeColor = Color.WHITE;
        this.textColor = Color.WHITE;
    }
    
    public Button(double x, double y, String text) {
        this(x, y, GameConstants.UI_BUTTON_WIDTH, GameConstants.UI_BUTTON_HEIGHT, text);
    }
    
    public void update(I_InputProvider inputProvider) {
        if (inputProvider == null) {
            isHovered = false;
            isClicked = false;
            return;
        }
        
        int mouseX = inputProvider.getMouseX();
        int mouseY = inputProvider.getMouseY();
        
        isHovered = mouseX >= x && mouseX <= x + width &&
                   mouseY >= y && mouseY <= y + height;

        isClicked = isHovered && inputProvider.isMouseClicked();
    }
    
    public void render(GraphicsContext gc) {
        if (gc == null) return;
        
        TextAlignment previousAlignment = gc.getTextAlign();
        
        try {
            // Draw background
            Color currentBgColor = isHovered ? hoverBackgroundColor : backgroundColor;
            gc.setFill(currentBgColor);
            gc.fillRoundRect(x, y, width, height, 10, 10);
            
            // Draw stroke
            Color currentStrokeColor = isHovered ? hoverStrokeColor : strokeColor;
            gc.setStroke(currentStrokeColor);
            gc.setLineWidth(2);
            gc.strokeRoundRect(x, y, width, height, 10, 10);
            
            // Draw text
            gc.setTextAlign(TextAlignment.CENTER);
            TextRenderer.drawOutlinedText(
                gc,
                text,
                x + width / 2,
                y + height / 2 + 8,
                font,
                textColor,
                Color.color(0, 0, 0, 0.85),
                1.5,
                new DropShadow(6, Color.color(0, 0, 0, 0.6))
            );
        } finally {
            gc.setTextAlign(previousAlignment);
        }
    }

    public boolean isHovered() {
        return isHovered;
    }
    
    public boolean isClicked() {
        return isClicked;
    }

    public boolean contains(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width &&
               mouseY >= y && mouseY <= y + height;
    }
    
    public double getX() {
        return x;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getY() {
        return y;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public double getWidth() {
        return width;
    }
    
    public void setWidth(double width) {
        this.width = width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setHeight(double height) {
        this.height = height;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Font getFont() {
        return font;
    }
    
    public void setFont(Font font) {
        this.font = font;
    }
    
    public void setColors(Color backgroundColor, Color hoverBackgroundColor, 
                         Color strokeColor, Color hoverStrokeColor, Color textColor) {
        this.backgroundColor = backgroundColor;
        this.hoverBackgroundColor = hoverBackgroundColor;
        this.strokeColor = strokeColor;
        this.hoverStrokeColor = hoverStrokeColor;
        this.textColor = textColor;
    }
}

