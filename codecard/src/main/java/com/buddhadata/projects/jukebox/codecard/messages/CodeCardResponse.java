package com.buddhadata.projects.jukebox.codecard.messages;

import com.buddhadata.projects.jukebox.codecard.messages.enums.Background;
import com.buddhadata.projects.jukebox.codecard.messages.enums.BackgroundColor;
import com.buddhadata.projects.jukebox.codecard.messages.enums.Icon;
import com.buddhadata.projects.jukebox.codecard.messages.enums.Template;

/**
 * The deserialized message which is understood by the Code Card
 */
public class CodeCardResponse {

    /**
     * possible backgrounds to use on Code Card
     */
    private Background background;

    /**
     * background color of the code card
     */
    private BackgroundColor backgroundColor;

    /**
     * the main body/message of the response displayed by the Code Card
     */
    private String bodytext;

    /**
     * the icon to display
     */
    private Icon icon;

    /**
     * subtitle to display on code card
     */
    private String subtitle;

    /**
     * template/format of what is displayed, all I can find is there is 1-11 but no idea what's on each
     */
    private Template template = Template.template1;

    /**
     * title to display on code card.
     */
    private String title;

    /**
     * getter
     * @return possible backgrounds to use on Code Card
     */
    public Background getBackground() {
        return background;
    }

    /**
     * setter
     * @param background possible backgrounds to use on Code Card
     */
    public void setBackground(Background background) {
        this.background = background;
    }

    /**
     * getter
     * @return background color of the code card
     */
    public BackgroundColor getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * setter
     * @param backgroundColor background color of the code card
     */
    public void setBackgroundColor(BackgroundColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * getter
     * @return the main body/message of the response displayed by the Code Card
     */
    public String getBodytext() {
        return bodytext;
    }

    /**
     * setter
     * @param bodytext the main body/message of the response displayed by the Code Card
     */
    public void setBodytext(String bodytext) {
        this.bodytext = bodytext;
    }

    /**
     * getter
     * @return the icon to display
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * setter
     * @param icon the icon to display
     */
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    /**
     * getter
     * @return subtitle to display on Code Card
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * setter
     * @param subtitle subtitle to display on Code Card
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * getter
     * @return template/format of what is displayed, all I can find is there is 1-11 but no idea what's on each
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * setter
     * @param template template/format of what is displayed, all I can find is there is 1-11 but no idea what's on each
     */
    public void setTemplate(Template template) {
        this.template = template;
    }

    /**
     * getter
     * @return title to display on Code Card
     */
    public String getTitle() {
        return title;
    }

    /**
     * setter
     * @param title to display on Code Card
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
