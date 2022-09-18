package algoritmo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Função responsável pelo cálculo da distância ("Manhattan Distance")
 * entre duas coordenadas: "origem" e "destino".
 */
class HScore {
    public static int hScore(int[] originCoordinates, int[] destinyCoordinates) {
        return Math.abs(originCoordinates[0] - destinyCoordinates[0])
                + Math.abs(originCoordinates[1] - destinyCoordinates[1]);
    }
}

/**
 * Representação básica de um nó.
 */
class Node {
    // O rótulo do vértice.
    public String label;
    // O vértice parente do vértice atual.
    public Node root;

    // Construtor base.
    Node(String label, Node root) {
        this.label = label;
        this.root = root;
    }
}

/**
 * Representação básica de um grafo.
 */
class Graph {
    // Armazena os vértices do grafo.
    protected Map<String, Map<String, Integer>> vertexes = new HashMap<>();

    /**
     * Adiciona um vértice ao grafo,
     * se não existir.
     * 
     * @param label O rótulo do vértice.
     */
    public void addVertexToGraph(String label) {
        this.vertexes.putIfAbsent(label, new HashMap<>());
    }

    /**
     * Adiciona uma aresta entre dois vértices.
     * 
     * @param originLabel  O rótulo do vértice origem.
     * @param destinyLabel O rótulo do vértice destino.
     * @param direction    A direção do vértice orige
     */
    public void addEdgeToGraph(String originLabel, String destinyLabel, int direction) {
        this.vertexes.get(originLabel).put(destinyLabel, direction);
    }

    /**
     * Pega os vértices vizinhos de um vértice fornecido
     * qualquer.
     * 
     * @param label O rótulo de um vértice qualquer do grafo.
     * @return Os vértices vizinhos de um vértice.
     */
    public Map<String, Integer> getVertexNeighbor(String label) {
        return this.vertexes.get(label);
    }

    /**
     * Remove o separador do rótulo, transformando-os
     * para inteiro e retordando-os.
     * 
     * @param label O rótulo do vértice.
     * @return Uma lista de inteiros com as coordenadas.
     */
    public int[] labelToCoordinates(String label) {
        // Remove o separador.
        String[] coordinates = label.split(":");
        // Transforma para uma lista de inteiro e retorna-os.
        return new int[] {
                Integer.parseInt(coordinates[0]),
                Integer.parseInt(coordinates[1])
        };
    }

    /**
     * Transforma as coordenadas de um vértice qualquer
     * em seu possível rótulo para o grafo.
     * 
     * @param coordinates As coordenadas do vértice.
     * @return O rótulo do vértice.
     */
    public String coordinatesToLabel(int[] coordinates) {
        // Transforma as coordenadas de um vértice qualquer
        // em um rótulo adptado para o grafo.
        return Integer.toString(coordinates[0]) + ":" + Integer.toString(coordinates[1]);
    }

    /**
     * Reconstroi o caminho percorrido pelo A*.
     * 
     * @param path         Os vértices visitados.
     * @param destinyLabel O rótulo do vértice destino.
     * @return Uma lista contendo o caminho encontrado pelo A*.
     */
    public ArrayList<String> reconstructPath(ArrayList<Node> path, String destinyLabel) {

        // Armazena o caminho reconstruido.
        ArrayList<String> reconstructedPath = new ArrayList<>();

        // Pega o vértice destino.
        Node destiny;
        for (Node node : path) {
            if (node.label.equals(destinyLabel)) {
                destiny = node;

                // Itera sobre os vértices "raízes".
                while (destiny != null) {
                    reconstructedPath.add(destiny.label);
                    destiny = destiny.root;
                }

                // Sai do loop.
                break;
            }
        }

        // Reverte a ordem do caminho.
        Collections.reverse(reconstructedPath);
        // Retorna o caminho reconstruido.
        return reconstructedPath;
    }

    /**
     * Algoritmo de Busca A*.
     * 
     * @param origin  O rótulo do vértice origem.
     * @param destiny O rótulo do vértice destino.
     * @return O menor caminho da origem para o destino.
     */
    public ArrayList<String> AStar(String origin, String destiny) {
        // Os vértices descobertos que ainda podem ser expandidos.
        Map<String, Integer> openSet = new HashMap<String, Integer>() {
            {
                // Inclui o vértice origem já na lista.
                put(origin, 0);
            }
        };

        // Os vértices que já foram expandidos.
        ArrayList<String> closedSet = new ArrayList<>();

        // O caminho percorrido.
        ArrayList<Node> path = new ArrayList<>();

        // Caso o Ladrão já esteja no destino ou seja impossível de visitar.
        if (origin.equals(destiny) || this.getVertexNeighbor(destiny) == null) {
            return null;
        }

        // Responsável pela escolhe um vértice, de forma aleatória.
        Random vertexSelector = new Random();

        // Itera sobre os vértices descobertos.
        while (!(openSet.isEmpty())) {
            // A menor distância registrada.
            int minDistance = Collections.min(openSet.entrySet(), Map.Entry.comparingByValue()).getValue();
            // Cria uma lista somente com os vértices que possuem a menor distância.
            ArrayList<String> vertexesWithMinDistance = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : openSet.entrySet()) {
                // O rótulo do vértice.
                String vertex = entry.getKey();
                // A distância do vértice.
                int distance = entry.getValue();

                if (distance == minDistance) {
                    vertexesWithMinDistance.add(vertex);
                }
            }

            // Escolhe um vértice aleatório.
            String current = vertexesWithMinDistance.get(vertexSelector.nextInt(vertexesWithMinDistance.size()));

            // Verifica se chegou no objetivo.
            if (current.equals(destiny)) {
                return this.reconstructPath(path, destiny);
            }

            // Remove o vértice escolhido do "openSet".
            openSet.remove(current);

            // Adiciona o vértice ao caminho.
            path.add(new Node(current, null));

            // As coordenadas do vértice atual.
            int[] currentCoordinates = this.labelToCoordinates(current);

            // Expande os vizinhos.
            for (String neighbor : this.getVertexNeighbor(current).keySet()) {
                if (!(closedSet.contains(neighbor))) {
                    if (!(openSet.containsKey(neighbor))) {
                        // As coordendas do vizinho.
                        int[] neighborCoordinates = this.labelToCoordinates(neighbor);

                        // Altera o vértice "caminho".
                        for (Node node : path) {
                            if (node.label == current) {
                                path.add(new Node(neighbor, node));
                                break;
                            }
                        }

                        // Adiciona o vizinho ao "openSet".
                        openSet.put(
                                neighbor,
                                HScore.hScore(currentCoordinates, neighborCoordinates));
                    }
                }
            }
            // Adiciona o vértice atual ao "closedSet".
            closedSet.add(current);
        }
        // Sem solução.
        return null;
    }
}

/**
 * Representação de um agente inteligente, o Ladrão.
 */
public class Ladrao extends ProgramaLadrao {
    // A memória do Ladrão, referente ao Labirinto.
    protected int[][] knownField;

    // O grafo relacionado à memória do Ladrão.
    protected Graph graph;

    // O local em que o Ladrão pretende chegar durante sua exploração.
    private String explorationObjectiveLocation;

    // Define um "temporizador" de roubo para os Poupadores.
    private Map<Integer, Integer> targetRefreshRate;

    // Define a quantia prévia relacionada as moedas do Ladrão.
    private int previousMoneyOnHold;

    // Os terrenos impossíveis de visitar.
    protected ArrayList<Integer> nonVisitableLands = new ArrayList<>(
            Arrays.asList(
                    -1, // Sem visão para o terreno.
                    1, // Parede.
                    3, // Banco.
                    4, // Moeda.
                    5, // Pastilha do Poder.
                    200, // O Ladrão 1.
                    210, // O Ladrão 2.
                    220, // O Ladrão 3.
                    230 // O Ladrão 4.
            ));

    /**
     * Inicializa a variável de memória,
     * referente ao Labirinto.
     */
    private void initUnknownTerritoryVar() {
        this.knownField = new int[30][30];
        for (int[] field : this.knownField) {
            Arrays.fill(field, -2);
        }
    }

    /**
     * Construtor base da classe Ladrão,
     * inicializa as demais variáveis.
     */
    Ladrao() {
        this.initUnknownTerritoryVar();
        this.targetRefreshRate = new HashMap<Integer, Integer>() {
            {
                put(100, 0); // O Poupador 0.
                put(110, 0); // O Poupador 1.
            }
        };
        this.previousMoneyOnHold = this.getThiefMoneyOnHold();
    }

    /**
     * Pega os terrenos adjacentes a um terreno fornecido qualquer,
     * ajustando-os para à visão.
     * 
     * @param x A coordenada "x" do terreno.
     * @param y A coordenada "y" do terreno.
     * @return Os terrenos adjacentes que são possíveis de visitar.
     */
    private Map<String, Integer> getAdjacentLandsToVision(int x, int y) {
        // Os valores que, se somados a posição do terreno
        // fornecido, isto é, o 'x' e o 'y', retornarão os
        // terrenos adjacentes.
        int[] adjacentLandsIndex = new int[] { 0, -1, 0, 1, 1, 0, -1, 0 };

        // Armazena os terrenos adjacentes.
        Map<String, Integer> adjacentLands = new HashMap<>();

        // A direção referente ao terreno adjacente.
        // Ordem padrão: 1, 2, 3, 4. (os terrenos adjacentes são percorridos nessa
        // ordem.)
        int landTravelDirection = 1;

        // Itera sobre os pares de índices ('x' e 'y') dos terrenos adjacentes.
        for (int i = 0; i < adjacentLandsIndex.length; i += 2) {
            // As coordenadas do terreno adjacentes.
            int adjacentLandX = adjacentLandsIndex[i] + x;
            int adjacentLandY = adjacentLandsIndex[i + 1] + y;

            // Valida se as coordenadas estão dentro do labirinto.
            if (0 <= adjacentLandX && adjacentLandX <= 29) {
                if (0 <= adjacentLandY && adjacentLandY <= 29) {
                    // Verifica se o terreno é visitável.
                    if (!this.isLandInvalid(adjacentLandX, adjacentLandY)) {
                        // Adiciona o terreno atual e sua direção aos terrenos vizinhos.
                        adjacentLands.put(
                                this.graph.coordinatesToLabel(new int[] { adjacentLandX, adjacentLandY }),
                                landTravelDirection);
                    }
                }
            }
            // Altera a direção correspondente ao terreno adjacentes.
            landTravelDirection++;
        }

        // Retorna os terrenos adjacentes.
        return adjacentLands;
    }

    /**
     * Pega os terrenos adjacentes a um terreno fornecido qualquer,
     * ajustando-os para à memória.
     * 
     * @param x A coordenada "x" do terreno.
     * @param y A coordenada "y" do terreno.
     * @return Os terrenos adjacentes que são possíveis de visitar.
     */
    private Map<String, Integer> getAdjacentLandsToMemory(int x, int y) {
        // Os valores que, se somados a posição do terreno
        // fornecido, isto é, o 'x' e o 'y', retornarão os
        // terrenos adjacentes.
        int[] adjacentLandsIndex = new int[] { 0, -1, 0, 1, 1, 0, -1, 0 };

        // Armazena os terrenos adjacentes.
        Map<String, Integer> adjacentLands = new HashMap<>();

        // A direção referente ao terreno adjacente.
        // Ordem padrão: 4, 3, 2, 1 (os terrenos adjacentes são percorridos nessa
        // ordem.)
        int landTravelDirection = 4;

        // Itera sobre os pares de índices ('x' e 'y') dos terrenos adjacentes.
        for (int i = 0; i < adjacentLandsIndex.length; i += 2) {
            // As coordenadas do terreno adjacentes.
            int adjacentLandX = adjacentLandsIndex[i] + x;
            int adjacentLandY = adjacentLandsIndex[i + 1] + y;

            // Valida se as coordenadas estão dentro do labirinto.
            if (0 <= adjacentLandX && adjacentLandX <= 29) {
                if (0 <= adjacentLandY && adjacentLandY <= 29) {
                    // Verifica se o terreno é visitável.
                    if (!this.isLandInvalid(adjacentLandX, adjacentLandY)) {
                        // Adiciona o terreno atual e sua direção aos terrenos vizinhos.
                        adjacentLands.put(
                                this.graph.coordinatesToLabel(new int[] { adjacentLandX, adjacentLandY }),
                                landTravelDirection);
                    }
                }
            }
            // Altera a direção correspondente ao terreno adjacentes.
            landTravelDirection--;
        }

        // Retorna os terrenos adjacentes.
        return adjacentLands;
    }

    /**
     * Cria um grafo, baseando-se na memória do Ladrão.
     */
    private void updateGraphBasedOnMemory() {
        // Cria um novo grafo vazio.
        this.graph = new Graph();

        // Percorre os terrenos da memória do Ladrão.
        for (int y = 0; y <= this.knownField.length - 1; y++) {
            for (int x = 0; x <= this.knownField[y].length - 1; x++) {
                // Verifica se o terreno atual é visitável.
                if (!this.isLandInvalid(y, x)) {
                    // Adiciona um novo vértice ao grafo.
                    String currentLand = this.graph.coordinatesToLabel(new int[] { y, x });
                    this.graph.addVertexToGraph(currentLand);

                    // Pega os terrenos adjacentes ao terreno atual.

                    for (Map.Entry<String, Integer> entry : this.getAdjacentLandsToMemory(y, x).entrySet()) {
                        // O rótulo do terreno adjacente.
                        String adjVertex = entry.getKey();
                        // O valor de direção do terreno adjacente.
                        int adjVertexDirection = entry.getValue();

                        // Adiciona o vértice visinho ao grafo.
                        this.graph.addVertexToGraph(adjVertex);

                        // Adiciona uma aresta entre o terreno atual e o vizinho.
                        this.graph.addEdgeToGraph(currentLand, adjVertex, adjVertexDirection);
                    }
                }
            }
        }
    }

    /**
     * Cria um grafo, baseando-se na visão do Ladrão.
     */
    private void updateGraphBasedOnVision() {
        // Cria um novo grafo vazio.
        this.graph = new Graph();

        // Percorre todos os terrenos relacionados à visão do Ladrão.
        // Pega a posição ('x' e 'y') do Ladrão.
        int[] positions = this.getThiefCurrentPosition();
        int thiefX = positions[0];
        int thiefY = positions[1];

        // Percorre os terrenos da visão do Ladrão.
        for (int y = thiefY - 2; y <= thiefY + 2; y++) {
            for (int x = thiefX - 2; x <= thiefX + 2; x++) {
                // Verifica se o terreno alvo está dentro do Labirinto.
                if (0 <= x && x <= 29) {
                    if (0 <= y && y <= 29) {
                        // Verifica se o terreno atual é visitável.
                        if (!this.isLandInvalid(y, x)) {
                            // Adiciona um novo vértice ao grafo.
                            String currentLand = this.graph.coordinatesToLabel(new int[] { x, y });
                            this.graph.addVertexToGraph(currentLand);

                            // Pega os terrenos adjacentes ao terreno atual.
                            for (Map.Entry<String, Integer> entry : this.getAdjacentLandsToVision(x, y).entrySet()) {
                                // O rótulo do terreno adjacente.
                                String adjVertex = entry.getKey();
                                // O valor de direção do terreno adjacente.
                                int adjVertexDirection = entry.getValue();

                                // Adiciona o vértice visinho ao grafo.
                                this.graph.addVertexToGraph(adjVertex);

                                // Adiciona uma aresta entre o terreno atual e o vizinho.
                                this.graph.addEdgeToGraph(currentLand, adjVertex, adjVertexDirection);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Verifica se um Poupador passou
     * por perto, baseando-se no seu
     * cheiro.
     * 
     * @return Um valor lógico referente à
     *         passagem recente de um Poupador.
     */
    protected boolean isTargetStinking() {
        // Se o "cheiro" for diferente de 0, significa que
        // um Poupador passou por lá.
        return Arrays.stream(this.getSaverSmell()).anyMatch(i -> i != 0);
    }

    /**
     * Verifica se há um Poupador na visão
     * do Ladrão.
     * 
     * @return Um valor lógico referente à
     *         existência de um poupador na visão
     *         do Ladrão.
     */
    protected boolean isTargetOnSight() {
        // Retorna verdadeiro se houver pelo menos "100" ou "110",
        // na visão atual do Ladrão.
        return Arrays.stream(this.getThiefCurrentVision()).anyMatch(i -> i == 100 || i == 110);
    }

    /**
     * Pega a localização, o "x" e "y" do Poupador
     * que está dentro da visão do Ladrão.
     * 
     * @return As coordenadas, "x" e "y" do Ladrão.
     */
    protected int pinpointTargetLocation() {
        // Pega a posição ('x' e 'y') do Ladrão.
        int[] positions = this.getThiefCurrentPosition();
        int thiefX = positions[0];
        int thiefY = positions[1];
        
        // Pega a posição do Poupador se o Ladrão consegue vê-lo.
        if (this.isTargetOnSight()) {
            // Percorre os terrenos da visão do Ladrão.
            for (int y = thiefY - 2; y <= thiefY + 2; y++) {
                for (int x = thiefX - 2; x <= thiefX + 2; x++) {
                    // Verifica se o terreno alvo está dentro do Labirinto.
                    if (0 <= x && x <= 29) {
                        if (0 <= y && y <= 29) {
                            // Verifica se há algum Poupador nos terrenos da visão
                            // do Ladrão que não foi roubado recentemente.
                            if (this.knownField[y][x] == 100 && this.targetRefreshRate.get(100) == 0) {
                                // Retorna a posição do Poupador.
                                return this.pursueTarget(new int[] { x, y });
                            } else if (this.knownField[y][x] == 110 && this.targetRefreshRate.get(110) == 0) {
                                // Retorna a posição do Poupador.
                                return this.pursueTarget(new int[] { x, y });
                            }
                        }
                    }
                }
            }
        }
        // Pega a posição do Poupador se o Ladrão consegue sentir seu cheiro.
        else if (this.isTargetStinking()) {
            // O olfato do Ladrão.
            int[] saverSmell = this.getSaverSmell();
            // O índice do olfato do Ladrão.
            int saverSmellIndex = 0;
            // O menor olfato encontrado.
            int minSaverSmell = Integer.MAX_VALUE;
            // A posição do menor olfato encontrado.
            int[] minSmellPosition = null;

            // Itera sobre as linhas e colunas do olfato do Ladrão.
            for (int y = thiefY - 1; y <= thiefY + 1; y++) {
                for (int x = thiefX - 1; x <= thiefX + 1; x++) {
                    // Ignora a posição atual do Ladrão.
                    if (!(x == thiefX && y == thiefY)) {
                        // Verifica se o terreno alvo está dentro do Labirinto.
                        if (0 <= x && x <= 29) {
                            if (0 <= y && y <= 29) {
                                // Verifica se o cheiro encontrado é menor, ignorando os valores menores que
                                // "0".
                                if (saverSmell[saverSmellIndex] <= minSaverSmell
                                        && !(saverSmell[saverSmellIndex] <= 0)) {
                                    // Atualiza o menor cheiro encontrado.
                                    minSaverSmell = saverSmell[saverSmellIndex];
                                    // Atualiza a posição do menor cheiro encontrado.
                                    minSmellPosition = new int[] { x, y };
                                }
                            }
                        }
                        // Aumenta o índice do terreno correspondente ao cheiro.
                        saverSmellIndex++;
                    }
                }
            }

            // Retorna a posição do menor cheiro encontrado ou vai explorar o labirinto.
            return minSmellPosition != null ? this.pursueTarget(minSmellPosition) : this.exploreLabyrinth();
        }
        // O Ladrão vai explorar.
        return this.exploreLabyrinth();
    }

    /**
     * Verifica se determinada posição possui
     * um terreno possível de visitar.
     * 
     * @param x As coordenadas "x" do terreno.
     * @param y As coordenadas "y" do terreno.
     * @return Um valor lógico referente à
     *         disponibilidade do terreno.
     */
    protected boolean isLandInvalid(int x, int y) {
        return nonVisitableLands.contains(this.knownField[x][y]);
    }

    /**
     * Verifica se determinada posição possui
     * um terreno desconhecido.
     * 
     * @param x As coordenadas "x" do terreno.
     * @param y As coordenadas "y" do terreno.
     * @return Um valor lógico referente à desinformação do terreno.
     */
    protected boolean isLandUnknown(int x, int y) {
        return this.knownField[x][y] == -2;
    }

    /**
     * Verifica se determinada posição possui
     * um terreno conhecido.
     * 
     * @param x As coordenadas "x" do terreno.
     * @param y As coordenadsa "y" do terreno.
     * @return Um valor lógico referente à disponibilidade do terreno.
     */
    protected boolean isLandKnown(int x, int y) {
        return this.knownField[x][y] == 0;
    }

    /**
     * Verifica se o Ladrão já está no objetivo.
     * 
     * @return Um valor lógico referente à equalidade da posição do Ladrão com o
     *         objetivo.
     */
    protected boolean isThiefOnObjective() {
        return this.graph.coordinatesToLabel(this.getThiefCurrentPosition()).equals(this.explorationObjectiveLocation);
    }

    /**
     * Retorna uma lista de inteiros,
     * contendo a visão do Ladrão.
     * 
     * @return Uma lista de inteiros, referente à
     *         visão do Ladrão.
     */
    protected int[] getThiefCurrentVision() {
        return this.sensor.getVisaoIdentificacao();
    }

    /**
     * Retorna a quantidade de moedas do Ladrão.
     * 
     * @return Uma lista de inteiros, referente à
     *         quantia de moedas.
     */
    protected int getThiefMoneyOnHold() {
        return this.sensor.getNumeroDeMoedas();
    }

    /**
     * Retorna uma lista, indicando o cheiro
     * do Poupador, isto é, por onde o Poupador
     * passou.
     * 
     * @return Uma lista contendo o cheiro do Poupador.
     */
    protected int[] getSaverSmell() {
        return this.sensor.getAmbienteOlfatoPoupador();
    }

    /**
     * Retorna uma lista de inteiros, contendo
     * o 'x' e o 'y' do Ladrão.
     * 
     * @return Uma lista de inteiros, refente à
     *         posição do Ladrão.
     */
    protected int[] getThiefCurrentPosition() {
        // Pega a posição atual do Ladrão.
        java.awt.Point currentPosition = this.sensor.getPosicao();
        // Converte ambos os valores ('x' e 'y') para inteiro.
        int x = (int) currentPosition.getX();
        int y = (int) currentPosition.getY();
        // Retorna uma lista de inteiros, contendo ambas as coordenadas.
        return new int[] { x, y };
    }

    /**
     * Atualiza o "refresh rate" dos Poupadores alvos.
     */
    private void updateTargetRefreshRate() {
        for (Integer saverId : this.targetRefreshRate.keySet()) {
            if (this.targetRefreshRate.get(saverId) > 0) {
                this.targetRefreshRate.put(saverId, this.targetRefreshRate.get(saverId) - 1);
            }
        }
    }

    /**
     * Faz com que o Ladrão memorize todos
     * os terrenos que estão em seu campo de
     * visão.
     */
    private void memorizeVisitedLands() {
        // Pega a posição ('x' e 'y') do Ladrão.
        int[] positions = this.getThiefCurrentPosition();
        int thiefX = positions[0];
        int thiefY = positions[1];

        // A visão atual do Ladrão.
        int[] currentView = this.getThiefCurrentVision();

        // O índice dos terrenos na matriz de visão do Ladrão.
        int gridViewIndex = 0;

        // Itera sobre as linhas e colunas da visão do Ladrão.
        for (int y = thiefY - 2; y <= thiefY + 2; y++) {
            for (int x = thiefX - 2; x <= thiefX + 2; x++) {
                // Ignora a posição atual do Ladrão.
                if (!(x == thiefX && y == thiefY)) {
                    // Verifica se o terreno alvo está dentro
                    // do Labirinto.
                    if (0 <= x && x <= 29) {
                        if (0 <= y && y <= 29) {
                            // Evita o caso de o Ladrão "esquecer"
                            // de determinada informação sobre o terreno,
                            // pois as paredes bloqueiam a visão do Ladrão,
                            // e como consequência, ele associa a visão
                            // bloqueada como "desconhecida". Mas se ele
                            // já passou por lá, não faz sentido fazer ele
                            // "esquecer" de tal informação.
                            if ((currentView[gridViewIndex] != -2 && this.knownField[y][x] == -2) ||
                                    (currentView[gridViewIndex] != -2 && this.knownField[y][x] != -2)) {
                                this.knownField[y][x] = currentView[gridViewIndex];
                            }
                        }
                    }
                    // Aumenta o índice do terreno correspondente,
                    // na visão do Ladrão.
                    gridViewIndex++;
                } else {
                    // Adiciona um '0' na posição do Ladrão,
                    // pois a visão do Ladrão não inclui ele.
                    this.knownField[y][x] = 0;
                }
            }
        }
    }

    /**
     * Caso o Ladrão, veja ou sinta o cheiro de um Poupador,
     * ele persegue-o, até conseguir roubá-lo ou perder-lo.
     * 
     * @param targetLocation A posição ("x" e "y") do Poupador alvo.
     * @return O caminho a ser percorrido.
     */
    private int pursueTarget(int[] targetLocation) {
        // Cria um novo grafo baseado na visão do Ladrão.
        this.updateGraphBasedOnVision();
        // Verifica se o Ladrão roubou um Poupador.
        if (this.previousMoneyOnHold != this.getThiefMoneyOnHold()) {
            // O máximo de "refresh rate" que o Ladrão aceita.
            int maxRefreshRate = (int) Math.random() * (150 - 100) + 100;
            // O tanto de moedas que o Ladrão roubou do Poupador.
            int stoleCoins = this.getThiefMoneyOnHold() * 10;
            // Atualiza o "refresh rate" do Poupador roubado.
            if (stoleCoins == 0) {
                stoleCoins = (int) Math.random() * (10 - 30) + 10;
            }

            this.targetRefreshRate.put(this.knownField[targetLocation[1]][targetLocation[0]],
                    stoleCoins > maxRefreshRate ? maxRefreshRate : stoleCoins);
            // Altera o valor de "previousMoneyOnHold".
            this.previousMoneyOnHold = this.getThiefMoneyOnHold();
        }
        // Pega o menor caminho até o objetivo definido,
        // que no caso é a posição do Poupador, e percorre-o.
        return this.followAStarTrack(this.graph.coordinatesToLabel(this.getThiefCurrentPosition()),
                this.graph.coordinatesToLabel(targetLocation));
    }

    /**
     * Pega o terreno conhecido, a partir da memória
     * do Ladrão, mais distante em relação ao Ladrão.
     * 
     * @return O rótulo do vértice que representa o terreno conhecido mais
     *         distante.
     */
    protected String getLongestKnownVertex() {
        // Armazena os vértices conhecidos do grafo e as distâncias.
        Map<String, Integer> knownVerticesDistances = new HashMap<>();

        // Responsável pela seleção de um terreno qualquer para visitar.
        Random landSelector = new Random();

        // A posição atual do Ladrão.
        int[] currentThiefPosition = this.getThiefCurrentPosition();
        currentThiefPosition = new int[] { currentThiefPosition[1], currentThiefPosition[0] };

        // Itera sobre todos os vértices do grafo que representam
        // terrenhos conhecidos.
        for (String vertex : this.graph.vertexes.keySet()) {
            // Pega as coordenadas do vértice.
            int[] vertexCoordinates = this.graph.labelToCoordinates(vertex);
            // Verifica se o terreno é conhecido.
            if (this.isLandKnown(vertexCoordinates[0], vertexCoordinates[1])) {
                // Adiciona o vértice e a distância ao dicionário.
                knownVerticesDistances.put(vertex,
                        HScore.hScore(currentThiefPosition, vertexCoordinates));
            }
        }

        // Os vértices que ele conhece.
        ArrayList<String> vertexes = new ArrayList<>(knownVerticesDistances.keySet());
        if (!vertexes.isEmpty()) {    
            // Retorna qualquer vértice que ele conhece.
            return vertexes.get(landSelector.nextInt(vertexes.size()));
        }
        // Retorna o vértice conhecido mais distante, em relação ao Ladrão.
        else if (!knownVerticesDistances.isEmpty()) {    
            return Collections.max(knownVerticesDistances.entrySet(), Map.Entry.comparingByValue()).getKey();
        } 
        // Vai para o banco.
        else {
            return "8:8";
        }        
    }

    /**
     * Pega o terreno desconhecido, a partir da memória
     * do Ladrão, mais distante em relação ao Ladrão.
     * 
     * @return O rótulo do vértice que representa o terreno desconhecido mais
     *         distante.
     */
    protected String getLongestUnknownVertex() {
        // Armazena os vértices desconhecidos do grafo e as distâncias.
        Map<String, Integer> unknownVerticesDistances = new HashMap<>();

        // Responsável pela seleção de um terreno qualquer para visitar.
        Random landSelector = new Random();

        // A posição atual do Ladrão.
        int[] currentThiefPosition = this.getThiefCurrentPosition();

        // Itera sobre todos os vértices do grafo que representam
        // terrenhos desconhecidos.
        for (String vertex : this.graph.vertexes.keySet()) {
            // Pega as coordenadas do vértice.
            int[] vertexCoordinates = this.graph.labelToCoordinates(vertex);
            // Verifica se o terreno é desconhecido.
            if (this.isLandUnknown(vertexCoordinates[0], vertexCoordinates[1])) {
                // Adiciona o vértice e a distância ao dicionário.
                unknownVerticesDistances.put(
                        vertex,
                        HScore.hScore(currentThiefPosition, vertexCoordinates));
            }
        }
        // Retorna o rótulo do vértice mais distante
        // caso ainda exista rótulos desconhecidos.
        if (!unknownVerticesDistances.isEmpty()) {
            // A maior distância possível.
            int maxDistance = (int) Math
                    .floor(Collections.max(unknownVerticesDistances.entrySet(), Map.Entry.comparingByValue())
                            .getValue() / 2);
            // Cria uma lista somente com os vértices que possuem a maior distância.
            ArrayList<String> unknownVerticesMaxDistance = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : unknownVerticesDistances.entrySet()) {
                // O rótulo do vértice.
                String vertex = entry.getKey();
                // A distância do vértice.
                int distance = entry.getValue();

                if (distance >= maxDistance) {
                    unknownVerticesMaxDistance.add(vertex);
                }
            }

            // Pega um terreno desconhecido, dentre os mais distantes,
            // de forma aleatória, caso exista.
            if (unknownVerticesMaxDistance.size() > 1) {
                return unknownVerticesMaxDistance.get(landSelector.nextInt(unknownVerticesMaxDistance.size()));
            }
        }
        // Retorna o rótulo do vértice mais distante,
        // sendo ele um terreno conhecido qualquer.
        return this.getLongestKnownVertex();
    }

    /**
     * Caso o Ladrão não veja nem sinta o cheiro de um Poupador, o mesmo vai
     * explorar o Labirinto, caso ainda não tenha definido um caminho de
     * exploração.
     * 
     * @return O caminho a ser percorrido.
     */
    private int exploreLabyrinth() {
        // Cria um novo grafo baseado na memória do Ladrão.
        this.updateGraphBasedOnMemory();
        // Verifica se o Ladrão já definiu um local como objetivo ou
        // se o Ladrão já está no objetivo.
        if (this.explorationObjectiveLocation == null || this.isThiefOnObjective()) {
            // Pega o ponto mais distante, no labirinto, que ainda não foi visitado pelo
            // Ladrão.
            this.explorationObjectiveLocation = this.getLongestUnknownVertex();
        }
        // Pega o menor caminho até o objetivo definido, que no caso é o terreno
        // desconhecido mais distante em relação ao Ladrão.
        int[] thiefPosition = this.getThiefCurrentPosition();
        return this.followAStarTrack(
                this.graph.coordinatesToLabel(new int[] { thiefPosition[1], thiefPosition[0] }),
                this.explorationObjectiveLocation);
    }

    /**
     * Gera um caminho, utilizando o algoritmo A*,
     * dada um origem e um destino quaisquer, verificando,
     * também, se o mesmo retornou um caminho válido.
     * 
     * @param origin  As coordenadas ("x" e "y") da origem.
     * @param destiny As coordenadas ("x" e "y") do destino.
     * @return A direção do primeiro caminho gerado pelo A*.
     */
    private int followAStarTrack(String origin, String destiny) {
        // Pega o menor caminho, caso exista, através do algoritmo A*.
        ArrayList<String> path = this.graph.AStar(origin, destiny);
        // Verifica se um caminho foi gerado pelo algoritmo A*.
        if (path != null) {
            // Percorre a primeira direção do caminho.
            return this.graph.vertexes.get(path.get(0)).get(path.get(1));
        } else {
            // Define um outro objetivo e vai até ele.
            this.explorationObjectiveLocation = this.getLongestUnknownVertex();
        }
        // Pega o terreno conhecido mais distante.
        this.explorationObjectiveLocation = this.getLongestKnownVertex();
        return (int) Math.random() * 5;
    }

    @Override
    public int acao() {
        // Memoriza o terreno.
        this.memorizeVisitedLands();
        // Atualiza o "refresh rate" dos Poupadores.
        this.updateTargetRefreshRate();
        // Faz o Ladrão perseguir um Poupador ou explorar o Labirinto.
        return this.pinpointTargetLocation();
    }
}