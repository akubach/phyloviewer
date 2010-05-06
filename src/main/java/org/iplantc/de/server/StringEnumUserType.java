package org.iplantc.de.server;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.util.ReflectHelper;

/**
 * A generic UserType that handles String-based JDK 5.0 Enums.
 * 
 * @author Gavin King
 */
public class StringEnumUserType implements EnhancedUserType, ParameterizedType
{

	@SuppressWarnings("unchecked")
	private Class<Enum> enumClass;

	@SuppressWarnings("unchecked")
	public void setParameterValues(Properties parameters)
	{
		String enumClassName = parameters.getProperty("enumClassname");
		try
		{
			enumClass = ReflectHelper.classForName(enumClassName);
		}
		catch(ClassNotFoundException cnfe)
		{
			throw new HibernateException("Enum class not found", cnfe);
		}
	}

	@SuppressWarnings("unchecked")
	public Class returnedClass()
	{
		return enumClass;
	}

	public int[] sqlTypes()
	{
		return new int[] { Hibernate.STRING.sqlType() };
	}

	public boolean isMutable()
	{
		return false;
	}

	public Object deepCopy(Object value)
	{
		return value;
	}

	@SuppressWarnings("unchecked")
	public Serializable disassemble(Object value)
	{
		return (Enum)value;
	}

	public Object replace(Object original, Object target, Object owner)
	{
		return original;
	}

	public Object assemble(Serializable cached, Object owner)
	{
		return cached;
	}

	public boolean equals(Object x, Object y)
	{
		return x == y;
	}

	public int hashCode(Object x)
	{
		return x.hashCode();
	}

	@SuppressWarnings("unchecked")
	public Object fromXMLString(String xmlValue)
	{
		return Enum.valueOf(enumClass, xmlValue);
	}

	@SuppressWarnings("unchecked")
	public String objectToSQLString(Object value)
	{
		return '\'' + ((Enum)value).name() + '\'';
	}

	@SuppressWarnings("unchecked")
	public String toXMLString(Object value)
	{
		return ((Enum)value).name();
	}

	@SuppressWarnings("unchecked")
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws SQLException
	{
		String name = rs.getString(names[0]);
		return rs.wasNull() ? null : Enum.valueOf(enumClass, name);
	}

	@SuppressWarnings("unchecked")
	public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException
	{
		if(value == null)
		{
			st.setNull(index, Hibernate.STRING.sqlType());
		}
		else
		{
			st.setString(index, ((Enum)value).name());
		}
	}
}
