package com.kissthinker.text;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.op4j.Op;
import org.op4j.functions.ExecCtx;
import org.op4j.functions.FnString;
import org.op4j.functions.IFunction;

import com.kissthinker.collection.CollectionUtil;

/**
 * @author David Ainslie
 *
 */
public final class StringUtil
{
    /**
     * Convenience method to convert given string to {@link InputStream}
     *
     * @param string
     * @return InputStream
     */
    public static InputStream toInputStream(String string)
    {
        return new ByteArrayInputStream(string.getBytes());
    }

    /**
     *
     * @param objects
     * @return
     */
    public static String toString(Object... objects)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for (Object object : objects)
        {
            if (object != null)
            {
                if (object instanceof Collection<?>)
                {
                    Collection<?> collection = (Collection<?>)object;

                    if (!collection.isEmpty())
                    {
                        stringBuilder.append(CollectionUtil.toString(collection));
                    }
                }
                else if (object.getClass().isArray())
                {
                    Object[] array = (Object[])object;

                    if (array.length > 0)
                    {
                        stringBuilder.append(Arrays.toString((Object[])object));
                    }
                }
                else
                {
                    stringBuilder.append(object.toString());
                }
            }
        }

        return stringBuilder.toString();
    }

    /**
     *
     * @param object
     * @return
     */
    public static String toReflectString(Object object)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(object.getClass().getSimpleName()).append('[');

        boolean firstRound = true;

        for (Field field : object.getClass().getDeclaredFields())
        {
            if (!firstRound)
            {
                stringBuilder.append(", ");
            }

            firstRound = false;
            field.setAccessible(true);

            try
            {
                stringBuilder.append(field.getName()).append('=').append('\'').append(field.get(object)).append('\'');
            }
            catch (IllegalAccessException e)
            {
                // Ignore - naughty!
            }

        }

        stringBuilder.append(']');

        return stringBuilder.toString();
    }

    /**
     * Create a title from a given string.
     * The title will be in the format "My Title", i.e. capitilized words separated by spaces.
     * E.g.
     * camelCase -> Camel Case
     * MY_CAPS -> My Caps
     * Scooby Doo -> Scooby Doo
     * @param string
     * @return the string as a title
     */
    public static String title(String string)
    {
        String[] strings = StringUtils.splitByCharacterTypeCamelCase(string);

        // Solution 1.
        // strings = Op.on(strings).removeAllTrue(FnString.eq(" ")).removeAllTrue(FnString.eq("_")).map(new CapitilizeString()).get();

        // Solution 2.
        /*strings = Op.on(strings)
                    .removeAllTrue(FnString.eq(" "))
                    .removeAllTrue(FnString.eq("_"))
                    .map(FnString.toLowerCase())
                    .map(FnString.capitalize()).get();*/

        // return StringUtils.join(strings, " ");

        // Solution 3.
        // return Op.on(strings).map(new TitleFilter()).removeAllNull().reduce(FnReduce.onString().join(" ")).get(); DOES NOT WORK for empty array.
        strings = Op.on(strings).map(new TitleFilter()).removeAllNull().get();

        return StringUtils.join(strings, " ");
    }
    
    /**
     * Utility.
     */
    private StringUtil()
    {
        super();
    }
}

/**
 *
 * @author David Ainslie
 */
class TitleFilter implements IFunction<String, String>
{
    /** */
   private static final Set<String> FILTERS = Op.onSetFor("", " ", "_").get();

   /**
    * @see org.op4j.functions.IFunction#execute(java.lang.Object, org.op4j.functions.ExecCtx)
    */
   @Override
   public String execute(String string, ExecCtx execCtx) throws Exception
   {
       if (string == null)
       {
           return null;
       }
       else if (FILTERS.contains(string))
       {
           return null;
       }
       else
       {
           return Op.on(string).exec(FnString.toLowerCase()).exec(FnString.capitalize()).get();
       }
   }
}