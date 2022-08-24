package algoritmo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Ladrao extends ProgramaLadrao {

	// O terreno "memorizado" pelo ladrão.
	private int[][] knownField = new int[30][30];

	// A quantidade de visitas de cada terreno pelo ladrão.
	private int[][] traveledPath = new int[30][30];

	public Ladrao() {
		// Inicializa uma matriz multidimensional,
		// a qual representa o terreno conhecido pelo ladrão,
		// porém com todos os valores -2, ou seja, "desconhecidos".
		for (int[] field : knownField) {
			Arrays.fill(field, -2);
		}

		// Inicializa uma matriz multidimensional,
		// a qual representa a quantia de visitas em cada terreno, pelo ladrão,
		// porém com todos os valores 0, ou seja, nenhuma vez visitado.
		for (int[] path : traveledPath) {
			Arrays.fill(path, 0);
		}
	}

	@Override
	public int acao() {
		// "Memoriza" os terrenos visíveis pelo ladrão.
		this.memorizeLand(this.sensor.getVisaoIdentificacao(), this.sensor.getPosicao());
		// TODO: Doc e implementação da lógica de terreno menos explorado.
		return this.decidePath(this.sensor.getPosicao());
	}

	/**
	 * Explora os terrenos adjacentes ao ladrão, baseado na quantidade de vezes que
	 * cada terreno foi visitado.
	 * 
	 * @param nonVisitablePaths Os terrenos "impossíveis" de visitar.
	 * @param positionX         A posição "x" do ladrão.
	 * @param positionY         A posição "y" do ladrão.
	 * @return A direção em que o ladrão irá.
	 */
	private int gatherAdjacentLandsInfo(ArrayList<Integer> nonVisitablePaths, Integer positionX, Integer positionY) {
		// As informações sobre os terrenos adjacentes ao ladrão.
		ArrayList<Integer> adjacentLands = new ArrayList<Integer>();

		// Aumenta a quantidade de visitas do terreno atual.
		this.traveledPath[positionY][positionX]++;

		// A contagem de visitas dos terrenos adjacentes ao ladrão.
		Map<Integer, Integer> adjacentLandsTravelCount = new HashMap<>();

		// Os índices do seguintes terrenos: Norte, Sul, Oeste e Leste.
		int[] landsIndexing = new int[] { -1, 0, 1, 0, 0, 1, 0, -1 };

		// Representa o sentido do terreno: 1, 2, 3, 4.
		Integer landDirectionIndex = 1;

		// Itera sobre os terrenos adjacentes.
		for (int i = 1; i <= landsIndexing.length; i += 2) {
			// Os índices, x e y, dos terrenos adjacentes ao ladrão.
			Integer x = landsIndexing[i];
			Integer y = landsIndexing[i - 1];

			// Verifica se o terreno adjacente, na coordenada x, está dentro do labirinto.
			if (0 <= (x + positionX) && (x + positionX) <= 29) {
				// Verifica se o terreno adjacente, na coordenada y, está dentro do labirinto.
				if (0 <= (y + positionY) && (y + positionY) <= 29) {
					// Atualiza as informações e a quantidade de visitas dos terrenos adjacentes.
					adjacentLands.add(this.knownField[y + positionY][x + positionX]);
					adjacentLandsTravelCount.put(landDirectionIndex, this.traveledPath[y + positionY][x + positionX]);
				} else {
					// Case alguma terreno adjacente, na coordenada y, esteja fora do labirinto,
					// o valor -1 (terreno inválido) será adicionado.
					adjacentLands.add(-1);
					adjacentLandsTravelCount.put(landDirectionIndex, -1);
				}
			} else {
				// Case alguma terreno adjacente, na coordenada x, esteja fora do labirinto,
				// o valor -1 (terreno inválido) será adicionado.
				adjacentLands.add(-1);
				adjacentLandsTravelCount.put(landDirectionIndex, -1);
			}
			// Altera o valor responsável pelo sentido do terreno.
			landDirectionIndex++;
		}

		// Remove, das opções disponíveis, os terrenos adjacentes que são inválidos.
		for (int i = 0; i < adjacentLands.size(); i++) {
			if (nonVisitablePaths.contains(adjacentLands.get(i))) {
				adjacentLandsTravelCount.remove(i + 1);
			}
		}

		// O terreno adjacente com a menor quantia de visitas.
		Integer leastTraveledLand = Collections.min(adjacentLandsTravelCount.values());

		// Itera sobre os terrenos adjacentes.
		ArrayList<Integer> leastAdjacentTraveledLands = new ArrayList<Integer>();
		for (Map.Entry<Integer, Integer> entry : adjacentLandsTravelCount.entrySet()) {
			// Direção e quantia de visitas dos terrenos adjacentes.
			Integer landDirection = entry.getKey();
			Integer landTravelCount = entry.getValue();

			// Adiciona o terreno caso a quantia de visitas seja mínima.
			if (landTravelCount == leastTraveledLand) {
				leastAdjacentTraveledLands.add(landDirection);
			}
		}

		// Escolhe, dentre os terrenos adjacentes, de visitas mínimas, uma direção.
		Random landSelector = new Random();
		return leastAdjacentTraveledLands.get(landSelector.nextInt(leastAdjacentTraveledLands.size()));
	}

	/**
	 * TODO: Doc
	 * @param positionX
	 * @param positionY
	 * @return
	 */
	private ArrayList<Integer> gatherLandsKnownPercentage(Integer positionX, Integer positionY) {
		// O quanto que o ladrão sabe, em porcentagem, sobre cada direção.
		Map<Integer, Integer> directionPercentage = new HashMap<>();

		// O total de terrenos que o ladrão não conhece e o total que o ladrão consegue
		// ver.
		Integer unkownLandsCount = -1, totalLandsSeen = 0, directionValue = 0;

		// O valor das direções, de acordo com os loops.
		int[] directionsLoopOrder = new int[]{4, 3, 1, 2};

		// As posições do ladrão, o 'x' e o 'y'.
		int[] positions = new int[] { positionX, positionY };
		// Itera sobre ambas as posições do ladrão, o 'x' e o 'y';
		for (int posIndex = 0; posIndex < positions.length; posIndex++) {
			// Valores de incremento ou decremento, para os sentidos: Norte, Sul e Leste,
			// Oeste.
			for (Integer rowAndColDirections : new int[] { -1, 1 }) {
				// Itera sobre os terrenos adjacentes ao terreno percorrido em determinado
				// sentido, de modo, no total, 4 terrenos adjacentes sejam obtido.
				for (Integer rowAndColAdjacentLands = -2; rowAndColAdjacentLands <= 2; rowAndColAdjacentLands++) {
					// O sentido no qual será calculado a porcentagem de "conhecimento" dos
					// terrenos.
					Integer position = positions[posIndex];
					// Itera sobre os terrenos, percorridos em determinado sentido, até que atinga
					// alguma borda do labirinto.
					while (0 <= position && position <= 29) {
						// Ajusta a lógica de terrenos "desconhecidos" para ambos os eixos ('x' e 'y').
						if (posIndex == 0) {
							// Verifica se os terrenos adjacentes, de no máximo até 2 de distância, são
							// "desconhecidos".
							if (this.knownField[(rowAndColAdjacentLands + positionY)][position] == -2) {
								// Incrementa a quantia de terrenos "desconhecidos" caso seja verdadeiro.
								unkownLandsCount++;
							}
						} else {
							if (this.knownField[position][(rowAndColAdjacentLands + positionX)] == -2) {
								unkownLandsCount++;
							}
						}
						// Incrementa a quantia de terrenos vistos e a "direção".
						totalLandsSeen++;
						position += rowAndColDirections;
					}
				}
				// Reseta os valores para os demais loops e adiciona
				// a porcentagem de conhecimento de determinada direção.
				directionPercentage.put(directionsLoopOrder[directionValue], (unkownLandsCount * 100) / (totalLandsSeen - 5));
				directionValue++;
				totalLandsSeen = 0;
				unkownLandsCount = -1;
			}
		}

		// Armazena a chance de visitar determinada direção.
		ArrayList<Integer> pathTravelChances = new ArrayList<Integer>();
		// Itera sobre as direções e a chance de visitá-las.
		for (Map.Entry<Integer, Integer> entry : directionPercentage.entrySet()) {
			Integer directionTravelValue = entry.getKey();
			Integer directionChanceOfTraveling = entry.getValue();

			// Armazena os elementos, correspondentes a sua direção, "porcentagem" vezes.
			for (Integer i = directionChanceOfTraveling; i > 0; i--) {
				pathTravelChances.add(directionTravelValue);
			}
		}

		// Retorna uma lista com as direções, em que cada elemento
		// está contido "n" vezes. 
		// "n" representa a porcentagem de visitar o terreno.
		return pathTravelChances;
	}

	/**
	 * TODO: Doc aqui
	 * 
	 * @param currentPosition
	 * @return
	 */
	private int decidePath(java.awt.Point currentPosition) {
		// A posição atual do ladrão.
		Integer positionX = (int) currentPosition.getX();
		Integer positionY = (int) currentPosition.getY();

		// TODO: Remover os "200" pois representam o ladrao, eles vão ser usadas
		// posteriormentes.

		// Define os terrenos "impossíveis" de visitar.
		ArrayList<Integer> nonVisitablePaths = new ArrayList<>(
				Arrays.asList(-2, -1, 1, 3, 4, 5, 200, 210, 220, 230, 240));

		// TODO: DOC
		// FIXME: Problema descoberto: (Pensar em solução)
		// Se ele entra em um "beco sem saída", e a saída fica próximo de uma das bordas,
		// é garantido que o ladrão NUNCA vai sair de lá, pois ele já conhece a "saída"
		// mas não vai querer voltar pq CONHECE a "saída", ele vai ficar batendo contra a parede
		// na tentativa de ir para um caminho que ele NÃO CONHECE.
		ArrayList<Integer> landTravelProbability = this.gatherLandsKnownPercentage(positionX, positionY);

		// Explora os terrenos adjacentes e escolhe uma direção.
		return this.gatherAdjacentLandsInfo(nonVisitablePaths, positionX, positionY);
	}

	// TODO: DOC
	// private Map<Integer, Integer> getDirectionLandsKnownPercentage(Integer
	// positionX, Integer positionY) {
	// // O quanto que o ladrão conhece sobre cada direção, juntamente com a direção
	// // (chave).
	// Map<Integer, Integer> directionTravelChance = new HashMap<>();

	// // O total de terrenos desconhecidos e o total de terrenos.
	// Integer unknownLands = -1, totalLandsSeen = 0;

	// // Calcua o quanto o ladrão conhece sobre os terrenos da ESQUERDA.
	// for (int x = positionX; x >= 0; x--) {
	// for (int i = -2; i <= 2; i++) {
	// if ((i + positionY) >= 0 && (i + positionY) <= 29) {
	// if (this.knownField[i + positionY][x] == -2) {
	// unknownLands++;
	// }
	// totalLandsSeen++;
	// }
	// }
	// }
	// directionTravelChance.put(4, unknownLands * 100 / totalLandsSeen);
	// unknownLands = -1;
	// totalLandsSeen = 0;

	// // Calcula o quanto o ladrão conhece sobre os terrenos da DIREITA.
	// for (int x = positionX; x <= 29; x++) {
	// for (int i = -2; i <= 2; i++) {
	// if ((i + positionY) >= 0 && (i + positionY) <= 29) {
	// if (this.knownField[i + positionY][x] == -2) {
	// unknownLands++;
	// }
	// totalLandsSeen++;
	// }
	// }
	// }
	// directionTravelChance.put(3, unknownLands * 100 / totalLandsSeen);
	// unknownLands = -1;
	// totalLandsSeen = 0;

	// // Calcula o quanto o ladrão conhece sobre os terrenos ACIMA.
	// for (int y = positionY; y >= 0; y--) {
	// for (int i = -2; i <= 2; i++) {
	// if ((i + positionX) >= 0 && (i + positionX) <= 29) {
	// if (this.knownField[y][i + positionX] == -2) {
	// unknownLands++;
	// }
	// totalLandsSeen++;
	// }
	// }
	// }
	// directionTravelChance.put(1, unknownLands * 100 / totalLandsSeen);
	// unknownLands = -1;
	// totalLandsSeen = 0;

	// // Calcula o quanto o ladrão conhece sobre os terrenos ABAIXO.
	// for (int y = positionY; y <= 29; y++) {
	// for (int i = -2; i <= 2; i++) {
	// if ((i + positionX) >= 0 && (i + positionX) <= 29) {
	// if (this.knownField[y][i + positionX] == -2) {
	// unknownLands++;
	// }
	// totalLandsSeen++;
	// }
	// }
	// }
	// directionTravelChance.put(2, unknownLands * 100 / totalLandsSeen);
	// unknownLands = -1;
	// totalLandsSeen = 0;

	// // Eu fiquei com preguiça de reduzir os loops, desculpa.
	// return directionTravelChance;
	// }

	// // TODO: DOC
	// private void gatherAdjacentLandInfo(ArrayList<Integer> nonVisitablePaths,
	// Map<Integer, Integer> directionTravelChance, Integer positionX, Integer
	// positionY) {
	// // Armazenará as informações dos terrenos adjacentes ao ladrão.
	// ArrayList<Integer> adjacentLands = new ArrayList<Integer>();

	// // Armazenará a quantidade de vezes que o ladrão visitou determinado terreno.
	// Map<Integer, Integer> adjacentLandsTravelCount = new HashMap<>();

	// // Pega os caminhos adjacentes ao ladrão.
	// if ((positionY - 1) >= 0) { // Cima
	// adjacentLands.add(this.knownField[positionY - 1][positionX]);
	// adjacentLandsTravelCount.put(1, this.traveledPath[positionY - 1][positionX]);
	// }
	// if ((positionY + 1) <= 29) { // Baixo
	// adjacentLands.add(this.knownField[positionY + 1][positionX]);
	// adjacentLandsTravelCount.put(2, this.traveledPath[positionY + 1][positionX]);
	// }
	// if ((positionX + 1) <= 29) { // Direita
	// adjacentLands.add(this.knownField[positionY][positionX + 1]);
	// adjacentLandsTravelCount.put(3, this.traveledPath[positionY][positionX + 1]);
	// }
	// if ((positionX - 1) >= 0) { // Esquerda
	// adjacentLands.add(this.knownField[positionY][positionX - 1]);
	// adjacentLandsTravelCount.put(4, this.traveledPath[positionY][positionX - 1]);
	// }

	// // Ignora os terrenos "impossíveis" de visitar.
	// for (int i = 0; i < adjacentLands.size(); i++) {
	// if (nonVisitablePaths.contains(adjacentLands.get(i))) {
	// adjacentLandsTravelCount.remove(i + 1);
	// directionTravelChance.remove(i + 1);
	// }
	// }

	// // Pega o terreno que foi menos visitado pelo ladrão.
	// Integer leastTraveledLand =
	// Collections.min(adjacentLandsTravelCount.values());

	// // Cria uma lista somente com os terrenos menos visitados.
	// ArrayList<Integer> leastAdjacentTraveledLands = new ArrayList<Integer>();
	// for (Map.Entry<Integer, Integer> entry : adjacentLandsTravelCount.entrySet())
	// {
	// Integer landDirection = entry.getKey();
	// Integer landTravelCount = entry.getValue();

	// if (landTravelCount == leastTraveledLand) {
	// leastAdjacentTraveledLands.add(landDirection);
	// }
	// }

	// // Remove os caminhos "ineficientes".
	// for (int i = 1; i <= 4; i++) {
	// Integer landDirection = directionTravelChance.get(i);
	// if (!(leastAdjacentTraveledLands.contains(landDirection))) {
	// directionTravelChance.remove(landDirection);
	// }
	// }
	// }

	// private int calculateKnownPaths(Integer positionX, Integer positionY,
	// ArrayList<Integer> nonVisitablePaths) {
	// // O quanto o ladrão conhece sobre os terrenos em cada direção:
	// // Norte, Leste, Oeste e Sul.
	// Map<Integer, Integer> directionTravelChance =
	// this.getDirectionLandsKnownPercentage(positionX, positionY);

	// // TODO: DOC
	// this.gatherAdjacentLandInfo(nonVisitablePaths, directionTravelChance,
	// positionX, positionY);

	// // Define as possibilidades dos sentidos a ser seguido pelo ladrão.
	// ArrayList<Integer> pathPossibilities = new ArrayList<Integer>();
	// for (Map.Entry<Integer, Integer> entry : directionTravelChance.entrySet()) {
	// Integer directionValue = entry.getKey();
	// Integer directionChance = entry.getValue();

	// for (int i = Math.abs((100 - directionChance)); i > 0; i--) {
	// pathPossibilities.add(directionValue);
	// }
	// }

	// // Decide um sentido a seguir.
	// Random pathSelector = new Random();
	// return pathPossibilities.get(pathSelector.nextInt(pathPossibilities.size()));
	// }

	// private int decidePath(java.awt.Point currentPosition) {
	// // A posição atual do ladrão.
	// Integer positionX = (int) currentPosition.getX();
	// Integer positionY = (int) currentPosition.getY();

	// // Aumenta a quantidade de visitas do terreno atual.
	// this.traveledPath[positionY][positionX]++;

	// // Define os terrenos "impossíveis" de visitar.
	// ArrayList<Integer> nonVisitablePaths = new ArrayList<>(
	// Arrays.asList(-2, -1, 1, 3, 4, 5));

	// // Determina o quanto o ladrão sabe sobre os terrenos ao:
	// // Norte, Sul, Leste e Oeste.
	// return this.calculateKnownPaths(positionX, positionY, nonVisitablePaths);
	// }

	/**
	 * Armazena o último estado dos terrenos visíveis, pelo ladrão.
	 * 
	 * @param gridView        A visão do ladrão.
	 * @param currentPosition A posição atual do ladrão.
	 */
	private void memorizeLand(int[] gridView, java.awt.Point currentPosition) {
		// A posição atual (x e y) do ladrão.
		Integer positionX = (int) currentPosition.getX();
		Integer positionY = (int) currentPosition.getY();

		// O índice do elemento correspondente ao terreno visível.
		Integer gridIndex = 0;
		// Itera sobre cada terreno visível pelo ladrão.
		for (int y = positionY - 2; y <= positionY + 2; y++) {
			for (int x = positionX - 2; x <= positionX + 2; x++) {
				// Ignora a posição do ladrão.
				if (!(x == positionX && y == positionY)) {
					// Verifica se o terreno é válido.
					if (x >= 0 && x <= 29) {
						if (y >= 0 && y <= 29) {
							// "Memoriza" o terreno.
							this.knownField[y][x] = gridView[gridIndex];
						}
					}
					// Passa para o próximo terreno visível.
					gridIndex++;
				}
			}
		}
	}
}