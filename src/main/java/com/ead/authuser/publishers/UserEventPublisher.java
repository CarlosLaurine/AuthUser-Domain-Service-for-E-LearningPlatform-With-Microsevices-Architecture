package com.ead.authuser.publishers;

import com.ead.authuser.dtos.UserEventDTO;
import com.ead.authuser.enums.ActionType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserEventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value(value = "${ead.broker.exchange.user-event}")
    private String exchangeUserEvent;

    public void publishUserEvent(UserEventDTO userEventDTO, ActionType actionType) {
        userEventDTO.setActionType(actionType.toString());
        rabbitTemplate.convertAndSend(exchangeUserEvent, "", userEventDTO);
    }

}
