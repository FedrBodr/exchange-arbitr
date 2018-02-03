package ru.fedrbodr.exchangearbitr.utils;

public class NumberUtils {
	public static float round(float value, int scale) {
		int pow = 10;
		for (int i = 1; i < scale; i++) {
			pow *= 10;
		}
		float tmp = value * pow;
		float tmpSub = tmp - (int) tmp;

		return ( (float) ( (int) (
				value >= 0
						? (tmpSub >= 0.5f ? tmp + 1 : tmp)
						: (tmpSub >= -0.5f ? tmp : tmp - 1)
		) ) ) / pow;

		// Below will only handles +ve values
		// return ( (float) ( (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) ) ) / pow;
	}
	public static float round2(float value) {
		return round(value, 2);
	}
}
