package com.pixiv.util;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.AfterFilter;
import com.alibaba.fastjson.serializer.BeforeFilter;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;

/**
 * @author Anonymous(LHQ)
 *
 */
public class JsonUtils {

	/**
	 * 将java对象转换成json字符串
	 */
	public static String getJsonString4Object(Object object) {
		return JSON.toJSONString(object);
	}

	/**
	 * 将java对象转换成json字符串
	 */
	public static String getJsonString4Object(Object object, final String[] excludes) {
		return JSON.toJSONString(object, new PropertyFilter() {
			@Override
			public boolean apply(Object object, String name, Object value) {
				if (null != excludes) {
					for (String field : excludes) {
						if(field.equalsIgnoreCase(name))
							return false;
					}
				}

				return true;
			}
		});
	}

	@SuppressWarnings("unused")
	private static String toJSONString(Object object, SerializeFilter filter, String dateFormat, SerializerFeature... features) {
		SerializeWriter out = new SerializeWriter();
		try {
			JSONSerializer serializer = new JSONSerializer(out);

			for (com.alibaba.fastjson.serializer.SerializerFeature feature : features) {
				serializer.config(feature, true);
			}

			serializer.config(SerializerFeature.WriteDateUseDateFormat, true);

			if (dateFormat != null) {
				serializer.setDateFormat(dateFormat);
			}

			if (filter != null) {
				if (filter instanceof PropertyPreFilter) {
					serializer.getPropertyPreFilters().add((PropertyPreFilter) filter);
				}

				if (filter instanceof NameFilter) {
					serializer.getNameFilters().add((NameFilter) filter);
				}

				if (filter instanceof ValueFilter) {
					serializer.getValueFilters().add((ValueFilter) filter);
				}

				if (filter instanceof PropertyFilter) {
					serializer.getPropertyFilters().add((PropertyFilter) filter);
				}

				if (filter instanceof BeforeFilter) {
					serializer.getBeforeFilters().add((BeforeFilter) filter);
				}

				if (filter instanceof AfterFilter) {
					serializer.getAfterFilters().add((AfterFilter) filter);
				}
			}

			serializer.write(object);

			return out.toString();
		} finally {
			out.close();
		}
	}

	/**
	 * 将java对象转换成json字符串
	 */
	public static String getJsonString4Object(Object object, final String[] excludes, String dateFormat) {
		SerializerFeature features[] = {};
		SerializeFilter filter = new PropertyFilter() {
			@Override
			public boolean apply(Object object, String name, Object value) {
				if (null != excludes) {
					for (String field : excludes) {
						if(field.equalsIgnoreCase(name))
							return false;
					}
				}

				return true;
			}
		};

		SerializeWriter out = new SerializeWriter();
		try {
			JSONSerializer serializer = new JSONSerializer(out);

			for (com.alibaba.fastjson.serializer.SerializerFeature feature : features) {
				serializer.config(feature, true);
			}

			serializer.config(SerializerFeature.WriteDateUseDateFormat, true);

			if (dateFormat != null) {
				serializer.setDateFormat(dateFormat);
			}

			if (filter != null) {
				if (filter instanceof PropertyPreFilter) {
					serializer.getPropertyPreFilters().add((PropertyPreFilter) filter);
				}

				if (filter instanceof NameFilter) {
					serializer.getNameFilters().add((NameFilter) filter);
				}

				if (filter instanceof ValueFilter) {
					serializer.getValueFilters().add((ValueFilter) filter);
				}

				if (filter instanceof PropertyFilter) {
					serializer.getPropertyFilters().add((PropertyFilter) filter);
				}

				if (filter instanceof BeforeFilter) {
					serializer.getBeforeFilters().add((BeforeFilter) filter);
				}

				if (filter instanceof AfterFilter) {
					serializer.getAfterFilters().add((AfterFilter) filter);
				}
			}

			serializer.write(object);

			return out.toString();
		} finally {
			out.close();
		}
	}

	/**
	 * 将java对象转换成json字符串
	 */
	public static String getJsonString4ObjectUseDateFormat(Object object) {
		return JSON.toJSONString(object, SerializerFeature.WriteDateUseDateFormat);
	}

	/**
	 * 将java对象转换成json字符串
	 */
	public static String getJsonString4Object(Object object, String dateFormat) {
		return JSON.toJSONStringWithDateFormat(object, dateFormat);
	}

	/**
	 * 从一个JSON 对象字符格式中得到一个java对象
	 */
	public static <T> T getObject4JsonString(String jsonStr, Class<T> clazz) {
		T object = JSON.parseObject(jsonStr, clazz);
		return object;
	}

	/**
	 * 从一个JSON 对象字符格式中得到一个java对象
	 */
	public static <T> T getObject4JsonString(String jsonStr, Type type) {
		return JSON.parseObject(jsonStr, type);
	}

	/**
	 * 从一个JSON 对象字符格式中得到一个java对象
	 */
	public static <T> T getObject4JsonString(String jsonStr, TypeReference<T> type) {
		return JSON.parseObject(jsonStr, type);
	}

	/**
	 * 从一个JSON 对象字符格式中得到一个List集合
	 */
	public static List<Object> getList4JsonString(String jsonStr, Type[] types) {
		List<Object> list = JSON.parseArray(jsonStr, types);
		return list;
	}

	/**
	 * 从一个JSON 对象字符格式中得到一个List集合
	 */
	public static <T> List<T> getList4JsonString(String jsonStr, Class<T> clazz) {
		List<T> list = JSON.parseArray(jsonStr, clazz);
		return list;
	}

	/**
	 * 从一个JSON 对象字符格式中得到一个Map集合
	 */
	public static <K, V> Map<K, V> getMap4JsonString(String jsonStr, Class<K> kClazz, Class<V> vClazz) {
		Map<K, V> map = JSON.parseObject(jsonStr, new TypeReference<Map<K, V>>(){});
		return map;
	}

}
