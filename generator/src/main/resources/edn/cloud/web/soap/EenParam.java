package edn.cloud.web.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "param" })
@XmlRootElement(name = "Param")
public class EenParam {
	@SuppressWarnings("unused")
	private final static long serialVersionUID = 1L;

	@XmlElement(name = "param", namespace = "http://notification.event.successfactors.com")
	private Param[] param;

	public Param[] getParam() {
		return param;
	}

	public void setParam(Param[] param) {
		this.param = param;
	}
}
