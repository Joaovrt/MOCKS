package infra;

import br.com.valueprojects.mock_spring.model.Participante;
import infra.interfaces.IMessagingProvider;

public class SMSProvider implements IMessagingProvider {
  @Override
  public boolean sendMessage(Participante participante) {
    System.out.println("Parabéns "+participante.getNome()+"! Você foi o vencedor do jogo!");
    return true;
  }
}
