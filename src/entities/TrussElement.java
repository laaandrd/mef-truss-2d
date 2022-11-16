package entities;

import java.util.HashMap;

import utilities.MatrixOperations;

public class TrussElement {
	private int id; // indice do elemento

	/*
	 * PARAMETROS DO ELEMENTO
	 */
	private double youngModulus; 			// modulo de Young do elemento
	private double transversalArea; 		// area transversal do elemento
	private double length; 					// comprimento do elemento
	private double theta_rad; 				// angulo formado com o eixo de referencia
	public Double expansionCoefficient;		//coeficiente de expansao do elemento
	private double temperatureVariation;	//variacao de temperatura a qual o elemento esta sujeito
	private double stress; 					// tensao a qual o elemento esta sujeito
	private Node[] nodes = new Node[2];		//nos que compoem o elemento
	
	private double[][] rotationMatrix = new double[4][4];	//matriz de rotacao do elemento

	/*
	 * SISTEMA LOCAL
	 */
	private double[][] stiffnessMatrixLocalCoord = new double[4][4];	//matriz de rigidez do elemento em coordenadas locais

	/*
	 * SISTEMA GLOBAL
	 */
	private double[][] stiffnessMatrixGlobalCoord = new double[4][4];	//matriz de rigidez do elemento em coordenadas globais
	private double[][] thermalLoadArrayGlobalCoord = new double[4][1];	//vetor de carregamentos termicos no elemento em coordenadas globais

	public TrussElement() {
	}

	public TrussElement(int id, double e, double a, double l, double theta_rad) {
		this.id = id;
		this.youngModulus = e;
		this.transversalArea = a;
		this.length = l;
		this.theta_rad = theta_rad;
		this.expansionCoefficient = 0.0;
		this.temperatureVariation = 0.0;
		assembleRotationMatrix();
	}

	public TrussElement(int id, double e, double a, double l, double theta_rad, double alpha, double temperatureVariation) {
		this.id = id;
		this.youngModulus = e;
		this.transversalArea = a;
		this.length = l;
		this.theta_rad = theta_rad;
		this.expansionCoefficient = alpha;
		this.temperatureVariation = temperatureVariation;
		assembleRotationMatrix();
	}

	/*
	 * Metodo responsavel por montar a matriz de rotação do elemento
	 */
	public void assembleRotationMatrix() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (i == j) {
					rotationMatrix[i][j] = Math.cos(theta_rad);
				} else if ((i == 0 && j == 1) || (i == 2 && j == 3)) {
					rotationMatrix[i][j] = Math.sin(theta_rad);
				} else if ((i == 1 && j == 0) || (i == 3 && j == 2)) {
					rotationMatrix[i][j] = -Math.sin(theta_rad);
				} else {
					rotationMatrix[i][j] = 0;
				}
			}
		}
	}

	/*
	 * Metodo responsavel por montar a matriz de rigidez no sistema local
	 */
	public void assembleLocalCoordStiffnessMatrix() {

		HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
		hm.put(this.getNodeI().xTag(), 0);
		hm.put(this.getNodeI().yTag(), 1);
		hm.put(this.getNodeJ().xTag(), 2);
		hm.put(this.getNodeJ().yTag(), 3);

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (i == j && (i == hm.get(this.getNodeI().xTag()) || i == hm.get(this.getNodeJ().xTag()))) {
					stiffnessMatrixLocalCoord[i][j] = 1 * this.youngModulus * this.transversalArea / this.length;
				} else if ((i == hm.get(this.getNodeI().xTag()) && j == hm.get(this.getNodeJ().xTag()))
						|| (i == hm.get(this.getNodeJ().xTag()) && j == hm.get(this.getNodeI().xTag()))) {
					stiffnessMatrixLocalCoord[i][j] = -1 * this.youngModulus * this.transversalArea / this.length;
				} else {
					stiffnessMatrixLocalCoord[i][j] = 0;
				}
			}
		}
	}

	/*
	 * Metodo responsavel por montar a matriz de rigidez do elemento no sistema global
	 */
	public void assembleGlobalCoordStiffnessMatrix() {
		assembleLocalCoordStiffnessMatrix();
		double[][] transposedRotationMatrix = new double[4][4];
		MatrixOperations.transposeDoubleMatrix(rotationMatrix, transposedRotationMatrix);
		double[][] aux = new double[4][4];
		MatrixOperations.multiplyDoubleMatrix(transposedRotationMatrix, stiffnessMatrixLocalCoord, aux);
		MatrixOperations.multiplyDoubleMatrix(aux, rotationMatrix, stiffnessMatrixGlobalCoord);
	}

	/*
	 * Metodo responsavel por montar o vetor de carregamentos termicos do elemento no sistema global
	 */
	public void assembleGlobalCoordTermicLoadArray() {
		double aux = youngModulus * transversalArea * expansionCoefficient * temperatureVariation;
		thermalLoadArrayGlobalCoord[0][0] = -aux * Math.cos(theta_rad);
		thermalLoadArrayGlobalCoord[1][0] = -aux * Math.sin(theta_rad);
		thermalLoadArrayGlobalCoord[2][0] = aux * Math.cos(theta_rad);
		thermalLoadArrayGlobalCoord[3][0] = aux * Math.sin(theta_rad);
	}

	public void printGlobalStiffnessMatrix() {
		for (int i = 0; i < stiffnessMatrixGlobalCoord.length; i++) {
			for (int j = 0; j < stiffnessMatrixGlobalCoord[0].length; j++) {
				System.out.printf(stiffnessMatrixGlobalCoord[i][j] + "   ");
			}
			System.out.println();
		}
	}

	/*
	 * Metodo responsavel pelo calculo da tensao a qual o elemento esta sujeito
	 */
	public void calculateStress() {
		stress = (youngModulus / length)
				* ((Math.cos(theta_rad) * (nodes[1].getQx().doubleValue() - nodes[0].getQx().doubleValue())
						+ (Math.sin(theta_rad) * (nodes[1].getQy().doubleValue() - nodes[0].getQy().doubleValue()))))
				- youngModulus * expansionCoefficient * temperatureVariation;
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

	public double getE() {
		return youngModulus;
	}

	public void setE(double e) {
		this.youngModulus = e;
	}

	public double getA() {
		return transversalArea;
	}

	public void setA(double a) {
		this.transversalArea = a;
	}

	public double getL() {
		return length;
	}

	public void setL(double l) {
		this.length = l;
	}

	public double getTheta_rad() {
		return theta_rad;
	}

	public void setTheta_rad(double theta_rad) {
		this.theta_rad = theta_rad;
	}

	public Node getNodeI() {
		return this.nodes[0];
	}

	public Node getNodeJ() {
		return this.nodes[1];
	}

	public Node[] getNodes() {
		return nodes;
	}

	public void setNodes(Node nodeI, Node nodeJ) {
		this.nodes[0] = nodeI;
		this.nodes[1] = nodeJ;
	}

	public double[][] getStiffnessMatrixGlobalCoord() {
		return stiffnessMatrixGlobalCoord;
	}

	public double[][] getThermalLoadArrayGlobalCoord() {
		return thermalLoadArrayGlobalCoord;
	}

	public double getStress() {
		return stress;
	}
}