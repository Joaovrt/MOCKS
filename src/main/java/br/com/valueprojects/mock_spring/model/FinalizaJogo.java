package br.com.valueprojects.mock_spring.model;

import java.util.Calendar;
import java.util.List;

import infra.JogoDao;
import infra.interfaces.IMessagingProvider;



public class FinalizaJogo {

	private int total = 0;
	private final JogoDao dao;
	private IMessagingProvider messagingProvider;

	public FinalizaJogo(JogoDao dao) {
		this.dao = dao;
	}

	public FinalizaJogo(JogoDao dao, IMessagingProvider messagingProvider) {
		this.dao = dao;
		this.messagingProvider=messagingProvider;
	}

	public void finaliza() {
		List<Jogo> todosJogosEmAndamento = dao.emAndamento();

		for (Jogo jogo : todosJogosEmAndamento) {
			if (iniciouSemanaAnterior(jogo)) {
				jogo.finaliza();
				total++;
				dao.atualiza(jogo);
			}
		}
	}

	public void finalizaSalvaVencedorEnviaMensagem() {
		List<Jogo> todosJogosEmAndamento = dao.emAndamento();

		for (Jogo jogo : todosJogosEmAndamento) {
			if (iniciouSemanaAnterior(jogo)) {
				Juiz juiz = new Juiz();
				juiz.julga(jogo);
        Participante vencedor = juiz.getVencedor();
				jogo.setNomeVencedor(vencedor.getNome());
				jogo.finaliza();
				total++;
				try {
					dao.atualiza(jogo);
					this.messagingProvider.sendMessage(vencedor);
				}
				catch(RuntimeException e){
					System.out.println("Ocorreu um erro ao salvar o jogo: "+e);
				}
			}
		}
	}

	private boolean iniciouSemanaAnterior(Jogo jogo) {
		return diasEntre(jogo.getData(), Calendar.getInstance()) >= 7;
	}

	private int diasEntre(Calendar inicio, Calendar fim) {
		Calendar data = (Calendar) inicio.clone();
		int diasNoIntervalo = 0;
		while (data.before(fim)) {
			data.add(Calendar.DAY_OF_MONTH, 1);
			diasNoIntervalo++;
		}

		return diasNoIntervalo;
	}

	public int getTotalFinalizados() {
		return total;
	}
}
