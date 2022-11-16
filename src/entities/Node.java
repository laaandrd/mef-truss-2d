package entities;

public class Node {
	private int id; // identificador do no: em um problema, os nos devem ser numerados de forma
					// sequencial crescente

	private boolean isFixedOnXAxis; // indica se o no tem restricao de movimento no eixo x
	private boolean isFixedOnYAxis; // indica se o no tem restricao de movimento no eixo y

	/*
	 * deslocamentos nodais - podem assumir o valor null quando
	 * desconhecidos/nao-fixos
	 */
	private Double qx;
	private Double qy;

	/*
	 * forcas pontuais
	 */
	private Double fx;
	private Double fy;

	public Node() {
	}

	public Node(int id, Double qx, Double qy, Double fx, Double fy) {
		this.id = id;
		this.qx = qx;
		this.qy = qy;
		this.fx = fx;
		this.fy = fy;
		if (qx != null) {
			isFixedOnXAxis = (qx == 0.0);
		} else {
			isFixedOnXAxis = false;
		}
		if (qy != null) {
			isFixedOnYAxis = (qy == 0.0);
		} else {
			isFixedOnYAxis = false;
		}
	}

	/*
	 * Metodo responsavel por retornar o indice relativo a componente x do
	 * deslocamento nodal em um problema de muitos nos, calculado com base no
	 * identificador do no
	 */
	public int xTag() {
		return 2 * id - 1;
	}

	/*
	 * Metodo responsavel por retornar o indice relativo a componente y do
	 * deslocamento nodal em um problema de muitos nos, calculado com base no
	 * identificador do no
	 */
	public int yTag() {
		return 2 * id;
	}

	/*
	 * Metodos getters e setters - permitem acessar os atributos privados da classe
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Double getQx() {
		return qx;
	}

	public void setQx(Double qx) {
		this.qx = qx;
	}

	public Double getQy() {
		return qy;
	}

	public void setQy(Double qy) {
		this.qy = qy;
	}

	public Double getFx() {
		return fx;
	}

	public void setFx(Double fx) {
		this.fx = fx;
	}

	public Double getFy() {
		return fy;
	}

	public void setFy(Double fy) {
		this.fy = fy;
	}

	public boolean isFixedOnXAxis() {
		return isFixedOnXAxis;
	}

	public boolean isFixedOnYAxis() {
		return isFixedOnYAxis;
	}

}