package com.kalman03.gateway.doc;

import com.google.gson.Gson;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * @author Stranger
 * @since 2022-03-14
 */
public class Mock {

	public static void main(String[] args) {
		try {
			PodamFactory factory = new PodamFactoryImpl();
			UserDO simpleUserDO = factory.manufacturePojo(UserDO.class);
			System.out.println(new Gson().toJson(simpleUserDO));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
