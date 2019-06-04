package edn.cloud.business.dto.sfactor;

import java.util.ArrayList;

import edn.cloud.sfactor.persistence.entities.EventListenerParam;

public class SFEventListenerListDto {
	ArrayList<EventListenerParam> listEvents;

	public ArrayList<EventListenerParam> getListEvents() {
		return listEvents;
	}

	public void setListEvents(ArrayList<EventListenerParam> listEvents) {
		this.listEvents = listEvents;
	}
}
