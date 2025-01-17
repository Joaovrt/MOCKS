
package br.com.valueprojects.mock_spring.model;


public class Juiz {
	
	private double maisPontos = Double.NEGATIVE_INFINITY;
	private double menosPontos = Double.POSITIVE_INFINITY;
	private Participante vencedor;
	
	

	public void julga(Jogo jogo){
		if(jogo.getResultados().size()==0){
			throw new RuntimeException("Sem resultados n�o h� julgamento!");
			}
		for(Resultado resultado : jogo.getResultados()){
			if(resultado.getMetrica() > maisPontos){
				maisPontos = resultado.getMetrica();
				vencedor = resultado.getParticipante();
			}
			if(resultado.getMetrica() < menosPontos) menosPontos = resultado.getMetrica();

	     }
	}
		
	public double getPrimeiroColocado(){
			
			return maisPontos;
		}
		
   public double getUltimoColocado(){
			
			return menosPontos;
		}

	public Participante getVencedor(){
		return vencedor;
	}

}
