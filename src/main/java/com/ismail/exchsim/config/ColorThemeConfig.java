package com.ismail.exchsim.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Color Theme
 * 
 * @author ismail
 * @since 20221001
 */
@Data
@ConfigurationProperties("light")
public class ColorThemeConfig
{
    public String bodyText;

    public String bodyTextLight;

    public String bodyTextLight2;

    public String bodyLink;

    public String bodyVisitedLink;

    public String bodyActiveLink;

    public String bodyBg;

    public String formBg;

    public String formText;

    public String envText;

    public String fontHeader = "dddddd";

    public String bg = "ffffff";

    public String header = "EEEEEE";

    public String headerBg = "cccccc";

    public String headerFont = "000000";

    public String headerFontLight = "000000";

    public String headerFontLight2 = "000000";

    public String neutral = "black";

    public String positive = "green";

    public String positive2 = "darkgreen";

    public String negative = "red";

    public String warning = "yellow";

    public String warning2 = "orange";

    public String warning3 = "brown";

    public String light = "808080";

    public String light2 = "505050";

    public String light3 = "505050";

    public String tab0 = "E2EFFF";

    //public String colorTab0s = "6593CF";
    public String tab0s = "AAAACF";

    public String tab = "dddddd";

    public String tabs = "aaaaaa";

    public String tab0Text;

    public String tabText;

    public String bid = "73A2F7";

    public String ask = "C06E04";

    public String bgPositive = "B2FF00";

    public String bgPositive2 = "003300";

    public String bgNegative = "FCAAAA";

    public String bgNegative2 = "330000";

    public String bgNeutral = "cecece";

    public String bgNeutral2 = "333333";

    public String bgWarning = "Yellow";

    public String bgWarning2 = "Orange";

    public String raw;

    public String rawHighlight;

    public String rawHighlight2;

    public String rawSummary;

    public String sideText;

    // order active status
    public String orderActiveTrue;

    public String orderActiveFalse;

    // order status

    public String orderStatusNew;

    public String orderStatusFilled;

    public String orderStatusPartiallyFilled;

    public String orderStatusRejected;

    public String orderStatusCanceled;

    public String orderStatusExpired;

    public String orderStatusReleased;

    public ColorThemeConfig()
    {
        // System.out.println("--ColorTheme()");
    }

    /**
     * @param pOrderStatus
     * @return
     */
    public String getBgColorByPrimaryStatus(boolean pActive)
    {
        return (pActive ? orderActiveTrue : orderActiveFalse);
    }

    /**
     * @param pOrderStatus
     * @return
     */
    public String getBgColorBySecondaryStatus(String pOrderStatus, String pDefaultColor)
    {

        String color = bodyText;

        switch (pOrderStatus)
        {
        case "New":
            color = orderStatusNew;
            break;
        case "Filled":
            color = orderStatusFilled;
            break;

        case "PartiallyFilled":
            color = orderStatusPartiallyFilled;
            break;

        case "Rejected":
            color = orderStatusRejected;
            break;

        case "Canceled":
            color = orderStatusCanceled;
            break;

        case "Expired":
            color = orderStatusExpired;
            break;

        case "Released":
            color = orderStatusReleased;
            break;

        default:
            color = pDefaultColor;
        }

        return color;
    }
}
