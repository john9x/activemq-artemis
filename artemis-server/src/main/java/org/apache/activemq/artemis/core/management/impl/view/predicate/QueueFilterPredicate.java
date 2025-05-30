/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.artemis.core.management.impl.view.predicate;

import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.management.QueueControl;
import org.apache.activemq.artemis.core.management.impl.view.QueueField;
import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.apache.activemq.artemis.core.server.Consumer;
import org.apache.activemq.artemis.core.server.Queue;

public class QueueFilterPredicate extends ActiveMQFilterPredicate<QueueControl> {

   private QueueField f;

   private ActiveMQServer server;

   public QueueFilterPredicate(ActiveMQServer server) {
      super();
      this.server = server;
   }

   @Override
   public boolean test(QueueControl queue) {
      if (f == null)
         return true;
      try {
         return switch (f) {
            case ID -> matches(queue.getID());
            case NAME -> matches(queue.getName());
            case CONSUMER_ID -> {
               Queue q = server.locateQueue(SimpleString.of(queue.getName()));
               for (Consumer consumer : q.getConsumers()) {
                  if (matches(consumer.sequentialID()))
                     yield true;
               }
               yield false;
            }
            case MAX_CONSUMERS -> matches(queue.getMaxConsumers());
            case ADDRESS -> matches(queue.getAddress());
            case FILTER -> matches(queue.getFilter());
            case MESSAGE_COUNT -> matches(queue.getMessageCount());
            case CONSUMER_COUNT -> matches(queue.getConsumerCount());
            case DELIVERING_COUNT -> matches(queue.getDeliveringCount());
            case MESSAGES_ADDED -> matches(queue.getMessagesAdded());
            case MESSAGES_ACKED -> matches(queue.getMessagesAcknowledged());
            case MESSAGES_EXPIRED -> matches(queue.getMessagesExpired());
            case ROUTING_TYPE -> matches(queue.getRoutingType());
            case AUTO_CREATED -> matches(server.locateQueue(SimpleString.of(queue.getName())).isAutoCreated());
            case DURABLE -> matches(queue.isDurable());
            case PAUSED -> matches(queue.isPaused());
            case TEMPORARY -> matches(queue.isTemporary());
            case PURGE_ON_NO_CONSUMERS -> matches(queue.isPurgeOnNoConsumers());
            case MESSAGES_KILLED -> matches(queue.getMessagesKilled());
            case EXCLUSIVE -> matches(queue.isExclusive());
            case LAST_VALUE -> matches(queue.isLastValue());
            case SCHEDULED_COUNT -> matches(queue.getScheduledCount());
            case USER -> matches(queue.getUser());
            case INTERNAL_QUEUE -> matches(queue.isInternalQueue());
            default -> true;
         };
      } catch (Exception e) {
         return true;
      }
   }

   @Override
   public void setField(String field) {
      if (field != null && !field.isEmpty()) {
         this.f = QueueField.valueOfName(field);

         //for backward compatibility
         if (this.f == null) {
            this.f = QueueField.valueOf(field);
         }
      }
   }
}
