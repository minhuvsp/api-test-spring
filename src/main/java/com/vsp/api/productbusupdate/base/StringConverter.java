package com.vsp.api.productbusupdate.base;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This interface contains methods that can be used to convert String values into other data types.
 */
public interface StringConverter {
    /**
     * Default Date Formate
     */
    String JSON_DATE_FORMAT = "yyyy-M-d";
    String JSON_WRITE_DATE_FORMAT = "yyyy-MM-dd";
    String DEFAULT_DATE_FORMAT = "M/d/yyyy";

    /**
     * Converts the supplied String value into an Integer.
     *
     * @param value the source value.
     * @return see description.
     * @throws IllegalArgumentException thrown if the source string is improperly formatted.
     */
    default Integer convertToInteger(String value) throws IllegalArgumentException {

        if (StringUtils.isBlank(value)) {
            return null;
        }

        return Integer.parseInt(value);
    }

    /**
     * Converts the supplied String value into a Boolean.
     *
     * @param value the source value.
     * @return see description.
     * @throws IllegalArgumentException thrown if the source string is improperly formatted.
     */
    default Boolean convertToBoolean(String value) throws IllegalArgumentException {

        if (StringUtils.isBlank(value)) {
            return null;
        }

        return Boolean.parseBoolean(value);
    }

    /**
     * Converts the supplied String value into a Boolean.
     *
     * @param value        the source value.
     * @param defaultValue the default value if NULL.
     * @return see description.
     * @throws IllegalArgumentException thrown if the source string is improperly formatted.
     */
    default Boolean convertToBoolean(String value, boolean defaultValue) throws IllegalArgumentException {
        Boolean returnVal = convertToBoolean(value);

        return returnVal == null ? defaultValue : returnVal;
    }

    /**
     * Converts the supplied String value into a Date.
     *
     * @param value the source value.
     * @return see description.
     * @throws IllegalArgumentException thrown if the source string is improperly formatted.
     */
    default Date convertToDate(String value) throws IllegalArgumentException {
        return convertToDate(value, null);
    }

    /**
     * Converts the supplied String value into a Date.
     *
     * @param value the source value.
     * @return see description.
     * @throws IllegalArgumentException thrown if the source string is improperly formatted.
     */
    default Date convertToDate(String value, InputFormat format) throws IllegalArgumentException {

        if (StringUtils.isBlank(value)) {
            return null;
        }

        if (format == null) {
            if (value.indexOf("-") > -1) {
                format = InputFormat.JSON;
            } else {
                format = InputFormat.DEFAULT;
            }
        }

        try {
            DateFormat df = null;

            if (format == InputFormat.JSON) {
                df = new SimpleDateFormat(JSON_DATE_FORMAT);
            } else {
                df = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            }

            df.setLenient(false);
            return df.parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Coverts the supplied date to a string.
     *
     * @param date   the date to convert.
     * @param format the string date format.
     */
    default String convertToStringFromDate(Date date, InputFormat format) {
        DateFormat df = null;

        if (format == InputFormat.JSON) {
            df = new SimpleDateFormat(JSON_WRITE_DATE_FORMAT);
        } else {
            df = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        }

        df.setLenient(false);

        return df.format(date);
    }

    /**
     * Coverts the given date string from one date string format to another.
     *
     * @param value      the value to convert.
     * @param fromFormat the source format.
     * @param toFormat   the destination format.
     * @throws IllegalArgumentException
     */
    default String convertStringDate(String value, InputFormat fromFormat, InputFormat toFormat)
        throws IllegalArgumentException {
        Date date = null;
        String strDate = null;

        if (StringUtils.isBlank(value)) {
            return value;
        }

        if (toFormat == InputFormat.JSON && value.indexOf("-") > -1) {
            // no conversion needed.
            return value;
        }

        date = convertToDate(value, fromFormat);
        strDate = convertToStringFromDate(date, toFormat);

        return strDate;
    }

    /**
     * Coverts the given date string from one date string format to another.
     *
     * @param monthYearValue the value to convert.
     * @param toFormat       the destination format.
     * @throws IllegalArgumentException
     */
    default String convertMonthYearToString(String monthYearValue, InputFormat toFormat) {
        InputFormat fromFormat = InputFormat.DEFAULT;

        if (StringUtils.isBlank(monthYearValue)) {
            return monthYearValue;
        }

        String[] tokens = monthYearValue.split("/");
        String strDate = monthYearValue;

        // expecting MM/yyyy
        if (tokens.length == 2) {
            strDate = tokens[0] + "/01/" + tokens[1];
        }

        // does not match expected format
        return convertStringDate(strDate, fromFormat, toFormat);
    }

}
