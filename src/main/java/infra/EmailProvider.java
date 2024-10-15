package infra;

import br.com.valueprojects.mock_spring.model.Participante;
import infra.interfaces.IMessagingProvider;

public class EmailProvider implements IMessagingProvider {
  @Override
  public boolean sendMessage(Participante participante) {
    System.out.println("E-mail enviado para "+participante.getContato());
    System.out.println("Parabéns "+participante.getNome()+"! Você foi o vencedor do jogo");
    return true;
  }
}
