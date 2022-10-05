package com.ismail.exchsim.util;

import java.util.ArrayList;

/**
 * Utility
 * 
 * @author ismail
 * @since 20221001
 */
public class StringUtil
{
    /**
     * Parses items from a list delimited by a char 
     * 
     * @param text list
     * @param pDelimiter delimiter
     * @return String[] string array
     */
    public static String[] toArray(String text, char delimeter)
    {
        StringBuilder sbThisRow = new StringBuilder(200);

        ArrayList<String> outputArr = new ArrayList<>();

        if (text.length() > 0)
        {
            char c;

            for (int i = 0; i < text.length(); i++)
            {
                c = text.charAt(i);

                if (c == delimeter)
                {
                    String line = sbThisRow.toString().trim();
                    outputArr.add(line);

                    sbThisRow.setLength(0);
                }
                else
                {
                    sbThisRow.append(c);
                }
            }

            // last row (if any)
            if (sbThisRow.length() > 0)
            {
                String line = sbThisRow.toString().trim();
                outputArr.add(line);
            }
        }

        return outputArr.toArray(new String[outputArr.size()]);
    }

    public static final boolean isDefined(String exchID)
    {
        return (exchID != null && exchID.trim().length() > 0);
    }

}
