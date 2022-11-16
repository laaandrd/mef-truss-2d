package entities;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utilities.MatrixOperations;

public class TrussProblem {
	NumberFormat nf = new DecimalFormat("0.000E0");

	private List<Node> nodes = new ArrayList<Node>(); // indica todos os nos que compoem a estrutura
	private List<TrussElement> elements = new ArrayList<TrussElement>(); // indica os elementos de trelica que compoem o
																			// problema

	private double[][] globalStiffnessMatrix; // matriz de rigidez global
	private double[][] loadArray; // vetor de carregamentos
	private double[][] displacementArray; // vetor de deslocamentos
	private double[][] reactionForceArray; // vetor de forcas de reacao

	private double[][] reducedGlobalStiffnessMatrix; // matriz de rigidez global reduzida apos aplicar o metodo direto
	private double[][] invertedReducedGlobalStiffnessMatrix; // apos aplicado o metodo direto, a matriz reduzida obtida
																// e invertida para o calculo dos deslocamentos
	private double reducedLoadArray[][]; // o metodo direto tambem reduz o vetor de carregamentos
	private double[][] reducedDisplacement; // valores obtidos para os deslocamentos dos nos nao-fixos

	public TrussProblem() {
	}

	/*
	 * Metodo responsavel por montar o vetor de carregamentos
	 */
	private void assembleLoadArray() {
		this.loadArray = new double[nodes.size() * 2][1];
		int i = 0;
		// Carregamentos pontuais
		for (Node n : nodes) {
			loadArray[i][0] = n.getFx().doubleValue();
			loadArray[i + 1][0] = n.getFy().doubleValue();
			i += 2;
		}

		HashMap<Integer, Integer> individualToGlobal = new HashMap<Integer, Integer>(); // realiza o mapeamento entre os
																						// indices dos sitemas global e
																						// local
		// Carregamentos termicos
		for (TrussElement te : elements) {
			te.assembleGlobalCoordTermicLoadArray();
			individualToGlobal.put(0, te.getNodeI().xTag() - 1);
			individualToGlobal.put(1, te.getNodeI().yTag() - 1);
			individualToGlobal.put(2, te.getNodeJ().xTag() - 1);
			individualToGlobal.put(3, te.getNodeJ().yTag() - 1);
			for (int j = 0; j < 4; j++) {
				loadArray[individualToGlobal.get(j)][0] += te.getThermalLoadArrayGlobalCoord()[j][0];
			}
			individualToGlobal.clear();
		}
	}

	/*
	 * Metodo responsavel por montar o vetor de deslocamentos com base nos nos
	 * definidos para o problema
	 */
	private void assembleDisplacementArray() {
		this.displacementArray = new double[nodes.size() * 2][1];
		int i = 0;
		for (Node n : nodes) {
			displacementArray[i][0] = n.getQx().doubleValue();
			displacementArray[i + 1][0] = n.getQy().doubleValue();
			i += 2;
		}
	}

	/*
	 * Metodo responsavel por montar o vetor de forcas de reacao
	 */
	private void assembleReactionsForceArray() {
		this.reactionForceArray = new double[nodes.size() * 2][1];
		double[][] aux = new double[nodes.size() * 2][1];
		MatrixOperations.multiplyDoubleMatrix(globalStiffnessMatrix, displacementArray, aux);
		for (int i = 0; i < nodes.size() * 2; i++) {
			reactionForceArray[i][0] = aux[i][0] - loadArray[i][0];
		}
	}

	/*
	 * Metodo responsavel por montar a matriz global de rigidez
	 */
	private void assembleGlobalStiffnessMatrix() {
		this.globalStiffnessMatrix = new double[nodes.size() * 2][nodes.size() * 2];
		HashMap<Integer, Integer> individualToGlobal = new HashMap<Integer, Integer>(); // faz o mapeamento entre os
																						// indices dos sistemas global e
																						// local
		for (TrussElement te : elements) {
			individualToGlobal.put(0, te.getNodeI().xTag() - 1);
			individualToGlobal.put(1, te.getNodeI().yTag() - 1);
			individualToGlobal.put(2, te.getNodeJ().xTag() - 1);
			individualToGlobal.put(3, te.getNodeJ().yTag() - 1);
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					globalStiffnessMatrix[individualToGlobal.get(i)][individualToGlobal
							.get(j)] += te.getStiffnessMatrixGlobalCoord()[i][j];
				}
			}
			individualToGlobal.clear();
		}

	}
	
	/*
	 * Metodo responsavel por imprimir as matrizes de rigidez indivuais dos elementos no sistema global
	 */
	public void printIndividualStiffnessMatrices() {
		for (TrussElement te : elements) {
			System.out.println("Element " + te.getId());
			te.printGlobalStiffnessMatrix();
			System.out.println("\n");
		}
	}

	/*
	 * Metodo responsavel por adicionar um novo no ao problema
	 */
	public void addNode(Double qx, Double qy, Double fx, Double fy) {
		nodes.add(new Node(nodes.size() + 1, qx, qy, fx, fy));
	}

	/*
	 * Metodo responsavel por adicionar um novo elemento ao problema: os nos devem ser adicionados antes dos elementos
	 */
	public void addElement(int nodeI, int nodeJ, double e, double a, double l, double theta_rad) {
		elements.add(new TrussElement(elements.size() + 1, e, a, l, theta_rad));
		elements.get(elements.size() - 1).setNodes(nodes.get(nodeI - 1), nodes.get(nodeJ - 1));
		elements.get(elements.size() - 1).assembleGlobalCoordStiffnessMatrix();
	}

	/*
	 * Sobrecarga do metodo acima para elementos sujeitos a cargas termicas
	 */
	public void addElement(int nodeI, int nodeJ, double e, double a, double l, double theta_rad, Double alpha,
			double temperatureVariation) {
		elements.add(new TrussElement(elements.size() + 1, e, a, l, theta_rad, alpha, temperatureVariation));
		elements.get(elements.size() - 1).setNodes(nodes.get(nodeI - 1), nodes.get(nodeJ - 1));
		elements.get(elements.size() - 1).assembleGlobalCoordStiffnessMatrix();
	}

	/*
	 * Metodo responsavel por reduzir a matriz de rigidez global com base nas condicoes de contorno do problema
	 */
	public double[][] matrixWithBoundaryConditions() {
		int i = 0;
		HashMap<Integer, Integer> control = new HashMap<Integer, Integer>();
		for (Node n : nodes) {
			if (!n.isFixedOnXAxis()) {
				control.put(i, n.xTag() - 1);
				i++;
			}
			if (!n.isFixedOnYAxis()) {
				control.put(i, n.yTag() - 1);
				i++;
			}
		}
		double[][] result = new double[i][i];
		for (int j = 0; j < i; j++) {
			for (int k = 0; k < i; k++) {
				result[j][k] = globalStiffnessMatrix[control.get(j)][control.get(k)];
			}
		}
		return result;
	}

	/*
	 * Metodo responsavel por reduzir o vetor de carregamentos com base nas condicoes de contorno do problema
	 */
	public double[][] reducedLoadArray() {
		int i = 0;
		for (Node n : nodes) {
			if (!n.isFixedOnXAxis()) {
				i++;
			}
			if (!n.isFixedOnYAxis()) {
				i++;
			}
		}
		double[][] reducedLoadArray = new double[i][1];
		int j = 0;
		for (Node n : nodes) {
			if (!n.isFixedOnXAxis()) {
				reducedLoadArray[j][0] = loadArray[n.xTag() - 1][0];
				j++;
			}
			if (!n.isFixedOnYAxis()) {
				reducedLoadArray[j][0] = loadArray[n.yTag() - 1][0];
				j++;
			}
		}
		return reducedLoadArray;
	}

	/*
	 * Metodo responsavel por solucionar o problema de trelica 2D apos estarem bem definidos todos os componentes
	 */
	public void solve() {

		assembleGlobalStiffnessMatrix();	//PASSO 1 - MONTAR A MATRIZ DE RIGIDEZ GLOBAL
		assembleLoadArray();				//PASSO 2 - MONTAR A MATRIZ DE CARREGAMENTOS

		//PASSO 3 - APLICAR AS CONDICOES DE CONTORNO NA MATRIZ DE RIGIDEZ GLOBAL E NO VETOR DE CARREGAMENTOS
		this.reducedGlobalStiffnessMatrix = matrixWithBoundaryConditions();
		this.reducedLoadArray = reducedLoadArray();
		this.reducedDisplacement = new double[reducedLoadArray.length][1];
		//PASSO 4 - INVERTER A MATRIZ DE RIGIDEZ GLOBAL REDUZIDA
		this.invertedReducedGlobalStiffnessMatrix = MatrixOperations.inverse(reducedGlobalStiffnessMatrix);
		//PASSO 5 - MULTIPLICAR A MATRIZ INVERTIDA PELO VETOR DE CARREGAMENTOS REDUZIDO PARA OBTER O DESLOCAMENTO DOS NOS LIVRES
		this.reducedDisplacement = new double[reducedLoadArray.length][1];
		MatrixOperations.multiplyDoubleMatrix(invertedReducedGlobalStiffnessMatrix, reducedLoadArray, reducedDisplacement);
		//PASSO 6 - OS VALORES DESCONHECIDOS DE DESLOAMENTO (null) SAO SUBSTITUIDOS PELOS VALORES OBTIDOS
		int i = 0;
		for (Node n : nodes) {
			if (!n.isFixedOnXAxis()) {
				n.setQx(reducedDisplacement[i][0]);
				i++;
			}
			if (!n.isFixedOnYAxis()) {
				n.setQy(reducedDisplacement[i][0]);
				i++;
			}
		}
		//PASSO 7 - E MONTADO O VETOR DE DESLOCAMENTOS COMPLETO
		assembleDisplacementArray();
		//PASSO 8 - E MONTADO O VETOR DE FORCAS DE REACAO
		assembleReactionsForceArray();
		//PASSO 9 - SAO CALCULADAS AS TENSOES AS QUAIS OS ELEMENTOS ESTAO SUJEITOS
		for (TrussElement te : elements) {
			te.calculateStress();
		}
	}
	
	/*
	 * Metodos getters e setters - permitem acessar os atributos privados da classe
	 */
	public List<Node> getNodes() {
		return nodes;
	}

	public List<TrussElement> getElements() {
		return elements;
	}

	public double[][] getGlobalStiffnessMatrix() {
		return globalStiffnessMatrix;
	}

	public double[][] getLoadArray() {
		return loadArray;
	}

	public double[][] getReduced() {
		return reducedGlobalStiffnessMatrix;
	}

	public double[][] getInverted() {
		return invertedReducedGlobalStiffnessMatrix;
	}

	public double[][] getReducedLoadArray() {
		return reducedLoadArray;
	}

	public double[][] getReducedDisplacement() {
		return reducedDisplacement;
	}

	public double[][] getDisplacementArray() {
		return displacementArray;
	}

	public double[][] getReactionForceArray() {
		return reactionForceArray;
	}

}
