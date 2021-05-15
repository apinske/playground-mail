package eu.pinske.playground.playground.mail.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Mail {

	@Id
	@Column(length = 36)
	private String uuid;

	@Column(length = 512)
	private String sender;

	@Column(length = 512)
	private String recipient;

	@Column(length = 512)
	private String subject;

	@Column(length = 512)
	private String status;

	@OneToOne
	private Data reportData;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Data getReportData() {
		return reportData;
	}

	public void setReportData(Data reportData) {
		this.reportData = reportData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mail other = (Mail) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Mail [uuid=" + uuid + ", sender=" + sender + ", recipient=" + recipient + ", subject=" + subject
				+ ", status=" + status + "]";
	}
}
