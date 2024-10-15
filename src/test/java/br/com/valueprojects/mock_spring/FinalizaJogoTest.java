package br.com.valueprojects.mock_spring;




import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.valueprojects.mock_spring.builder.CriadorDeJogo;
import br.com.valueprojects.mock_spring.model.FinalizaJogo;
import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.model.Participante;
import br.com.valueprojects.mock_spring.model.Resultado;
import infra.JogoDao;
import infra.interfaces.IMessagingProvider;




public class FinalizaJogoTest {
	
	 @Test
	    public void deveFinalizarJogosDaSemanaAnterior() {

	        Calendar antiga = Calendar.getInstance();
	        antiga.set(1999, 1, 20);

	        Jogo jogo1 = new CriadorDeJogo().para("Ca�a moedas")
	            .naData(antiga).constroi();
	        Jogo jogo2 = new CriadorDeJogo().para("Derruba barreiras")
	            .naData(antiga).constroi();

	        // mock no lugar de dao falso
	        
	        List<Jogo> jogosAnteriores = Arrays.asList(jogo1, jogo2);

	        JogoDao daoFalso = mock(JogoDao.class);

	        when(daoFalso.emAndamento()).thenReturn(jogosAnteriores);

	        FinalizaJogo finalizador = new FinalizaJogo(daoFalso);
	        finalizador.finaliza();

	        assertTrue(jogo1.isFinalizado());
	        assertTrue(jogo2.isFinalizado());
	        assertEquals(2, finalizador.getTotalFinalizados());
	    }
	 
	 @Test
		public void deveVerificarSeMetodoAtualizaFoiInvocado() {

			Calendar antiga = Calendar.getInstance();
			antiga.set(1999, 1, 20);

			Jogo jogo1 = new CriadorDeJogo().para("Cata moedas").naData(antiga).constroi();
			Jogo jogo2 = new CriadorDeJogo().para("Derruba barreiras").naData(antiga).constroi();

			// mock no lugar de dao falso

			List<Jogo> jogosAnteriores = Arrays.asList(jogo1, jogo2);

			JogoDao daoFalso = mock(JogoDao.class);

			when(daoFalso.emAndamento()).thenReturn(jogosAnteriores);

			FinalizaJogo finalizador = new FinalizaJogo(daoFalso);
			finalizador.finaliza();

			verify(daoFalso, times(1)).atualiza(jogo1);
			//Mockito.verifyNoInteractions(daoFalso);
		}
	 
		@Test
		public void deveAtualizarJogoAntesDeEnviarMensagem() {
			// Configura os mocks
			JogoDao jogoDaoMock = mock(JogoDao.class);
			IMessagingProvider messagingProviderMock = mock(IMessagingProvider.class);
			FinalizaJogo finalizaJogo = new FinalizaJogo(jogoDaoMock, messagingProviderMock);
			Jogo jogoMock = mock(Jogo.class);
			Participante vencedor = new Participante("Vencedor");
	
			Calendar dataSemanaAnterior = Calendar.getInstance();
			dataSemanaAnterior.add(Calendar.DAY_OF_MONTH, -8);
	
			when(jogoDaoMock.emAndamento()).thenReturn(Arrays.asList(jogoMock));
			when(jogoMock.getData()).thenReturn(dataSemanaAnterior);
			when(jogoMock.getResultados()).thenReturn(List.of(new Resultado(vencedor, 10.0)));
	
			// Executa o método que deve ser testado
			finalizaJogo.finalizaSalvaVencedorEnviaMensagem();
	
			// Verifica se o jogo foi atualizado antes de enviar a mensagem
			verify(jogoDaoMock).atualiza(jogoMock);
			verify(messagingProviderMock).sendMessage(vencedor);
		}
		

		@Test
		public void naoDeveEnviarMensagemSeFalharAoSalvarJogo() {
			// Configura os mocks
			JogoDao jogoDaoMock = mock(JogoDao.class);
			IMessagingProvider messagingProviderMock = mock(IMessagingProvider.class);
			FinalizaJogo finalizaJogo = new FinalizaJogo(jogoDaoMock, messagingProviderMock);
			Jogo jogoMock = mock(Jogo.class);
			Participante vencedor = new Participante("Vencedor");

			Calendar dataSemanaAnterior = Calendar.getInstance();
			dataSemanaAnterior.add(Calendar.DAY_OF_MONTH, -8);

			when(jogoDaoMock.emAndamento()).thenReturn(Arrays.asList(jogoMock));
			when(jogoMock.getData()).thenReturn(dataSemanaAnterior);
			when(jogoMock.isFinalizado()).thenReturn(false);
			when(jogoMock.getResultados()).thenReturn(List.of(new Resultado(vencedor, 10.0)));

			// Simula a exceção ao tentar atualizar o jogo
			doThrow(new RuntimeException("Erro ao salvar")).when(jogoDaoMock).atualiza(jogoMock);

			// Executa o método que deve ser testado
			finalizaJogo.finalizaSalvaVencedorEnviaMensagem();

			// Verifica que o método de atualização foi chamado e que a mensagem não foi enviada
			verify(jogoDaoMock).atualiza(jogoMock);
			verifyNoInteractions(messagingProviderMock);
		}

		 
	}

 

	
	

	
