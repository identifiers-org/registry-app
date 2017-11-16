/*
 * MIRIAM Registry (Web Application)
 * The registry is an online resource created to catalogue biological data collections,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2011  BioModels.net (EMBL - European Bioinformatics Institute)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package uk.ac.ebi.miriam.db;


/**
 * <p>Very basic storage of URIs (including their type: URN or URL).
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2011  BioModels.net (EMBL - European Bioinformatics Institute)
 * <br />
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <br />
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * </dd>
 * </dl>
 * </p>
 *
 * @author Camille Laibe <camille.laibe@ebi.ac.uk>
 * @version 20111206
 */
public class URI implements Comparable <URI>{
    public enum URItype {URN, URL};
    private String value = new String();
    private URItype type = URItype.URL;
    private String convertPrefix = "";
    // three allowed values on deprecated field (hack to sort out official uri issue)
    // 0 = official, 1 = deprecated, 2 = other (non-deprecated and non-official)
    private int deprecated = 0;
    private boolean official = false;

    public URI(){}
    /**
     * Constructor
     * @param value
     * @param type, default: URL
     */
    public URI(String value, String type)
    {
        this.value = value;
        if (type.equalsIgnoreCase("URN"))
        {
            this.type = URItype.URN;
        }
        else
        {
            this.type = URItype.URL;   // default
        }
    }

    
    /**
     * Getter
     * @return the value
     */
    public String getValue()
    {
        return this.value;
    }
    
    /**
     * Setter
     * @param value the value to set
     */
    public void setValue(String value)
    {
        this.value = value;
    }
    
    /**
     * Getter
     * @return the type
     */
    public URItype getType()
    {
        return this.type;
    }
    
    /**
     * Setter
     * @param type the type to set
     */
    public void setType(URItype type)
    {
        this.type = type;
    }

    /**
     * Setter
     * @param type the type to set
     */
    public void setType(String type)
    {
        if (type.equalsIgnoreCase("URN"))
        {
            this.type = URItype.URN;
        }
        else
        {
            this.type = URItype.URL;   // default
        }
    }

    public String getTypeString(){
        if(this.type.equals(URItype.URN)){
            return "URN";
        }else{
            return "URL";
        }
    }

    public String getConvertPrefix() {
        return convertPrefix;
    }

    public void setConvertPrefix(String convertPrefix) {
        this.convertPrefix = convertPrefix;
    }

/*    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }*/

    public int getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(int deprecated) {
        this.deprecated = deprecated;
    }

    public boolean isOfficial() {
        return official;
    }

    public void setOfficial(boolean official) {
        this.official = official;
    }

    public int compareTo(URI uri) {
        return this.getDeprecated() - uri.getDeprecated();
    }

    @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder();
        tmp.append("       - URI:  " + getValue() + "\n");
        tmp.append("       - Deprecated:  " + getDeprecated() + "\n");
        tmp.append("       - Convert prefix:  " + getConvertPrefix() + "\n");
        tmp.append("       - Type:    " + getType().toString() + "\n");
        return tmp.toString();
    }
}
