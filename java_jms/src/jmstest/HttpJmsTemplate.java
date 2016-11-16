package jmstest;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class HttpJmsTemplate extends JmsTemplate {
	
	public HttpJmsTemplate(ConnectionFactory connectionFactory){
		super(connectionFactory);
	}
	
	/**
	 * ����Ĭ�ϵ�jmsTemplate�������޸�Ϊͨ��http��ͬ������
	 */
	protected Message doSendAndReceive(Session session, Destination destination, MessageCreator messageCreator)
			throws JMSException {
		//����destination��ȡ����Ӧ��http�����ַ��Ȼ��message���͹�ȥ��ͬʱ��ȡ��Ӧ����Ϣ����
		return new JmsMessage();
	}
	
}
