package application;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import entities.TrussElement;
import entities.TrussProblem;
import utilities.MatrixOperations;

public class Program {

	public static void main(String[] args) {

		TrussProblem tp = new TrussProblem(); // objeto que representa um problema generico de trelissa 2D
		NumberFormat nf = new DecimalFormat("0.000E0"); // formato numerico utilizado para imprimir os resultados

		// SAO ADICIONADOS OS NOS DO PROBLEMA DE INTERESSE
		// SAO ESPECIFICADOS OS DESLOCAMENTOS NODAIS NAS DIRECOES X E Y: 0 -> NO FIXO
		// NAQUELA DIRECAO; null -> NO NAO ESTA FIXO
		// SAO ESPECIFICADOS OS CARREGAMENTOS NODAIS
		tp.addNode(0.0, 0.0, 0.0, 0.0);
		tp.addNode(null, 0.0, 20000.0, 0.0);
		tp.addNode(null, null, 0.0, -25000.0);
		tp.addNode(0.0, 0.0, 0.0, 0.0);

		// SAO ADICIONADOS OS ELEMENTOS DO PROBLEMA DE INTERESSE, ESPECIFICANDO OS NOS
		// QUE O DEFINEM E SUAS CARACTERISTICAS
		tp.addElement(1, 2, 29.5 * 1000000, 1, 40, 0.0);
		tp.addElement(2, 3, 29.5 * 1000000, 1, 30, 1.5707963267948966192313216916397514420985846996875529104874);
		tp.addElement(1, 3, 29.5 * 1000000, 1, 50, 0.6435011087932843868028092287173226380415105911153123828656);
		tp.addElement(4, 3, 29.5 * 1000000, 1, 40, 0.0);

		System.out.println("===========================================================\nPROBLEM 01: Chandrupatla 4.1 (THIRD EDITION)");
		System.out.println("===========================================================\n");

		// PARA CADA ELEMENTO DO PROBLEMA, E IMPRESSO SUA MATRIZ DE RIGIDEZ EM
		// COORDENADAS GLOBAIS
		System.out.println("**** INDIVIDUAL STIFFNESS MATRIX IN GLOBAL COORDINATES****\n");
		for (TrussElement te : tp.getElements()) {
			System.out.println("Element " + te.getId());
			System.out.println("[Q" + te.getNodeI().xTag() + " Q" + te.getNodeI().yTag() + " Q" + te.getNodeJ().xTag()
					+ " Q" + te.getNodeJ().yTag() + "]\n");
			MatrixOperations.printDoubleMatrixWithFormat(te.getStiffnessMatrixGlobalCoord(), nf);
			System.out.println("------------------------------------\n");
		}
		System.out.println();

		// O PROBLEMA É RESOLVIDO POR MEIO DO MÉTODO DIRETO
		tp.solve();

		System.out.println("**** GLOBAL STIFFNESS MATRIX {K} ****\n");
		System.out.print("[Q1");
		for (int i = 2; i <= tp.getNodes().size() * 2; i++) {
			System.out.print(" Q" + i);
		}
		System.out.println("]\n");
		MatrixOperations.printDoubleMatrixWithFormat(tp.getGlobalStiffnessMatrix(), nf);
		System.out.println();
		System.out.println();

		/*
		 * System.out.println("**** REDUCED MATRIX APLLYING BOUNDARY CONDITIONS ****\n"
		 * ); MatrixOperations.printDoubleMatrixWithFormat(tp.getReduced(), nf);
		 * System.out.println(); System.out.println(); /*
		 * System.out.println("**** INVERSE OF REDUCED MATRIX ****");
		 * MatrixOperations.printDoubleMatrixWithFormat(tp.getInverted(), nf);
		 * System.out.println(); System.out.println();
		 */

		System.out.println("**** LOAD ARRAY {F} ****\n");
		System.out.print("[Q1");
		for (int i = 2; i <= tp.getNodes().size() * 2; i++) {
			System.out.print(" Q" + i);
		}
		System.out.println("]\n");
		MatrixOperations.printDoubleMatrixWithFormat(tp.getLoadArray(), nf);
		System.out.println();
		System.out.println();

		/*
		 * System.out.println("**** REDUCED LOAD ARRAY ****\n");
		 * MatrixOperations.printDoubleMatrixWithFormat(tp.getReducedLoadArray(), nf);
		 * System.out.println(); System.out.println();
		 * 
		 * System.out.println("**** REDUCED DISPLACEMENT ARRAY ****");
		 * MatrixOperations.printDoubleMatrixWithFormat(tp.getReducedDisplacement(),
		 * nf); System.out.println(); System.out.println();
		 */

		System.out.println("**** DISPLACEMENT ARRAY {Q} ****\n");
		System.out.print("[Q1");
		for (int i = 2; i <= tp.getNodes().size() * 2; i++) {
			System.out.print(" Q" + i);
		}
		System.out.println("]\n");
		MatrixOperations.printDoubleMatrixWithFormat(tp.getDisplacementArray(), nf);
		System.out.println();
		System.out.println();

		System.out.println("**** STRESS IN EACH ELEMENT ****\n");
		System.out.print("[E1");
		for (int i = 2; i <= tp.getElements().size(); i++) {
			System.out.print(" E" + i);
		}
		System.out.println("]\n");
		for (TrussElement te : tp.getElements()) {
			System.out.println("Element " + te.getId() + " -> Stress: " + nf.format(te.getStress()));
		}
		System.out.println();
		System.out.println();

		System.out.println("**** REACTION FORCE ARRAY {R} ****\n");
		System.out.print("[R1");
		for (int i = 2; i <= tp.getNodes().size() * 2; i++) {
			System.out.print(" R" + i);
		}
		System.out.println("]\n");
		MatrixOperations.printDoubleMatrixWithFormat(tp.getReactionForceArray(), nf);
		System.out.println();
		System.out.println();

		System.out.println(
				"===========================================================\nPROBLEM 02: Chandrupatla 4.2 (THIRD EDITION)");
		System.out.println("===========================================================\n");

		TrussProblem tp2 = new TrussProblem();

		tp2.addNode(0.0, 0.0, 0.0, 0.0);
		tp2.addNode(null, 0.0, 0.0, 0.0);
		tp2.addNode(null, null, 0.0, 0.0);
		tp2.addNode(0.0, 0.0, 0.0, 0.0);

		tp2.addElement(1, 2, 29.5 * 1000000, 1, 40, 0.0, 0.000006666666666666666667, 0.0);
		tp2.addElement(2, 3, 29.5 * 1000000, 1, 30, 1.5707963267948966192313216916397514420985846996875529104874,
				0.000006666666666666666667, 50.0);
		tp2.addElement(1, 3, 29.5 * 1000000, 1, 50, 0.6435011087932843868028092287173226380415105911153123828656,
				0.000006666666666666666667, 50.0);
		tp2.addElement(4, 3, 29.5 * 1000000, 1, 40, 0.0, 0.000006666666666666666667, 0.0);

		// PARA CADA ELEMENTO DO PROBLEMA, E IMPRESSO SUA MATRIZ DE RIGIDEZ EM
		// COORDENADAS GLOBAIS
		System.out.println("**** INDIVIDUAL STIFFNESS MATRIX IN GLOBAL COORDINATES****\n");
		for (TrussElement te : tp2.getElements()) {
			System.out.println("Element " + te.getId());
			System.out.println("[Q" + te.getNodeI().xTag() + " Q" + te.getNodeI().yTag() + " Q" + te.getNodeJ().xTag()
					+ " Q" + te.getNodeJ().yTag() + "]\n");
			MatrixOperations.printDoubleMatrixWithFormat(te.getStiffnessMatrixGlobalCoord(), nf);
			System.out.println("------------------------------------\n");
		}
		System.out.println();

		// O PROBLEMA É RESOLVIDO POR MEIO DO MÉTODO DIRETO
		tp2.solve();

		System.out.println("**** GLOBAL STIFFNESS MATRIX {K} ****\n");
		System.out.print("[Q1");
		for (int i = 2; i <= tp2.getNodes().size() * 2; i++) {
			System.out.print(" Q" + i);
		}
		System.out.println("]\n");
		MatrixOperations.printDoubleMatrixWithFormat(tp2.getGlobalStiffnessMatrix(), nf);
		System.out.println();
		System.out.println();

		/*
		 * System.out.println("**** REDUCED MATRIX APLLYING BOUNDARY CONDITIONS ****\n"
		 * ); MatrixOperations.printDoubleMatrixWithFormat(tp2.getReduced(), nf);
		 * System.out.println(); System.out.println(); /*
		 * System.out.println("**** INVERSE OF REDUCED MATRIX ****");
		 * MatrixOperations.printDoubleMatrixWithFormat(tp2.getInverted(), nf);
		 * System.out.println(); System.out.println();
		 */

		System.out.println("**** LOAD ARRAY {F} ****\n");
		System.out.print("[Q1");
		for (int i = 2; i <= tp2.getNodes().size() * 2; i++) {
			System.out.print(" Q" + i);
		}
		System.out.println("]\n");
		MatrixOperations.printDoubleMatrixWithFormat(tp2.getLoadArray(), nf);
		System.out.println();
		System.out.println();

		/*
		 * System.out.println("**** REDUCED LOAD ARRAY ****\n");
		 * MatrixOperations.printDoubleMatrixWithFormat(tp2.getReducedLoadArray(), nf);
		 * System.out.println(); System.out.println();
		 * 
		 * System.out.println("**** REDUCED DISPLACEMENT ARRAY ****");
		 * MatrixOperations.printDoubleMatrixWithFormat(tp2.getReducedDisplacement(),
		 * nf); System.out.println(); System.out.println();
		 */

		System.out.println("**** DISPLACEMENT ARRAY {Q} ****\n");
		System.out.print("[Q1");
		for (int i = 2; i <= tp2.getNodes().size() * 2; i++) {
			System.out.print(" Q" + i);
		}
		System.out.println("]\n");
		MatrixOperations.printDoubleMatrixWithFormat(tp2.getDisplacementArray(), nf);
		System.out.println();
		System.out.println();

		System.out.println("**** STRESS IN EACH ELEMENTS ****\n");
		System.out.print("[E1");
		for (int i = 2; i <= tp2.getElements().size(); i++) {
			System.out.print(" E" + i);
		}
		System.out.println("]\n");
		for (TrussElement te : tp2.getElements()) {
			System.out.println("Element " + te.getId() + " -> Stress: " + nf.format(te.getStress()));
		}
		System.out.println();
		System.out.println();

		System.out.println("**** REACTION FORCE ARRAY {R} ****\n");
		System.out.print("[R1");
		for (int i = 2; i <= tp2.getNodes().size() * 2; i++) {
			System.out.print(" R" + i);
		}
		System.out.println("]\n");
		MatrixOperations.printDoubleMatrixWithFormat(tp2.getReactionForceArray(), nf);
		System.out.println();
		System.out.println();
	}

}
