package infra.interfaces;

import br.com.valueprojects.mock_spring.model.Participante;

public interface IMessagingProvider {
   boolean sendMessage(Participante participante);
}
