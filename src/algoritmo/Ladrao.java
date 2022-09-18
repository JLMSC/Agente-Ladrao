package algoritmo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Representação de um vértice.
 */
class Vertex {
    // O rótulo do vértice atual.
    private String label;

    // O vértice anterior ao vértice atual.
    private Vertex previous;

    /**
     * Construtor base do vértice.
     * @param label O rótulo do vértice.
     */
    Vertex(String label) {
        this.label = label;
    }

    /**
     * Retorna o rótulo do vértice atual.
     * @return O rótulo do vértice atual.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Altera o vértice anterior do vértice atual.
     * @param previous O novo vértice anterior ao vértice atual.
     */
    public void setPrevious(Vertex previous) {
        this.previous = previous;
    }

    /**
     * Retorna o vértice anterior ao vértice atual.
     * @return O vértice anterior.
     */
    public Vertex getPrevious() {
        return this.previous;
    }
}

/**
 * Representação de um grafo.
 */
class Graph {
    // Armazena os vértices do grafo.
    protected Map<Vertex, Map<Vertex, Integer>> vertexes = new HashMap<>();

    /**
     * Adiciona um vértice ao grafo.
     * @param label O rótulo do vértice.
     */
    public void addVertex(String label) {
        // Verifica se o grafo possui vértices.
        if (!(this.vertexes.isEmpty())) {
            // Se o vértice ja existe no grafo.
            boolean vertexAreadyExists = this.getVertexByLabel(label) != null ? true : false;

            // Adiciona o vértice ao grafo caso não exista.
            if (!(vertexAreadyExists)) {
                this.vertexes.put(new Vertex(label), new HashMap<>());
            }
        } else {
            this.vertexes.put(new Vertex(label), new HashMap<>());
        }
    }

    /**
     * Adiciona uma aresta entre dois vértices.
     * @param originLabel O vértice origem.
     * @param destinyLabel O vértice destino.
     * @param dir O valor referente à direção do vértice.
     */
    public void addEdge(String originLabel, String destinyLabel, Integer dir) {
        // Pega o vértice alvo no grafo.
        Vertex originVertex = this.getVertexByLabel(originLabel);
        // Verifica se o vértice existe.
        if (originVertex != null) {
            // Adiciona o vizinho ao vértice.
            this.vertexes.get(originVertex).put(new Vertex(destinyLabel), dir);
        }
    }

    /**
     * Pega os vértices vizinhos de um vértice qualquer.
     * @param label O vértice a ser pego seus vizinhos.
     * @return Os vértices vizinhos do vértice fornecido,
     * ou 'null' caso não exista.
     */
    public Map<Vertex, Integer> getNeighbors(String label) {
        // Procura pelo vértice no grafo e retorna os vizinhos.
        return this.vertexes.get(this.getVertexByLabel(label));
    }

    /**
     * Procura pelo vértice alvo, iterando
     * sobre todos os vértices do grafo.
     * @param label O rótulo do vértice.
     * @return O vértice caso exista no grafo,
     * 'null' caso contrário.
     */
    public Vertex getVertexByLabel(String label) {
        // Procura pelo vértice alvo, iterando
        // sobre todos os vértices do grafo.
        Vertex targetVertex = null;
        for (Vertex vertex : this.vertexes.keySet()) {
            if (vertex.getLabel().equals(label)) {
                targetVertex = vertex;
                break;
            }
        }
        return targetVertex;
    }

    /**
     * Remove o separador do rótulo, transforma
     * para inteiro e retorna-os.
     * @param label O rótulo do vértice.
     * @return Uma lista de inteiros com as coordenadas.
     */
    public int[] labelToCoordinates(String label) {
        // Remove o separador.
        String[] coordinates = label.split(":");
        // Transforma para uma lista de inteiro e retorna-o.
        return new int[]{
            Integer.parseInt(coordinates[0]),
            Integer.parseInt(coordinates[1])
        };
    }

    /**
     * Transforma as coordenadas de um vértice,
     * em seu rótulo.
     * @param coordinates As coordenadas do vértice.
     * @return O rótulo do vértice.
     */
    public String coordinatesToLabel(int[] coordinates) {
        // Transforma para Strings, adiciona o separador e retorna.
        return Integer.toString(coordinates[0]) + ":" + Integer.toString(coordinates[1]);
    }

    /**
     * Pega o valor heurístico associado à um
     * vértice alvo qualquer.
     * @param current O vértice alvo.
     * @param objective O vértice objetivo.
     * @return O valor heurístico do vértice alvo ao vértice objetivo.
     */
    protected Integer hScore(String current, String objective) {
        // Pega o vértice alvo no grafo.
        Vertex currentVertex = this.getVertexByLabel(current);
        // Pega o vértice objetivo no graof.
        Vertex objectiveVertex = this.getVertexByLabel(objective);

        // Verifica se o vértice existe no grafo.
        if (currentVertex != null && objectiveVertex != null) {
            // As coordenadas do vértice alvo.
            int[] currentCoordinates = this.labelToCoordinates(current);
            // As coordenadas do vértice objetivo.
            int[] objectiveCoordinates = this.labelToCoordinates(objective);
            // O valor heurístico associado ao vértice alvo. "Manhattan Distance"
            return (
                Math.abs(currentCoordinates[0] - objectiveCoordinates[0]) +
                Math.abs(currentCoordinates[1] - objectiveCoordinates[1])
            );
        } else {
            return null;
        }
    }

    /**
     * Reconstroi o caminho percorrido pelo A* a partir 
     * do último vértice visitado, que no caso seria o 
     * vértice objetivo.
     * @param current O vértice objetivo, obtido pelo A*.
     * @param origin O vértice origem do Ladrão.
     * @return Uma lista contendo os vértices a serem
     * percorridos para chegar no objetivo a partir da
     * origem.
     */
    public ArrayList<String> reconstructPath(Vertex current, String origin) {
        // Armazena o caminho.
        ArrayList<String> path = new ArrayList<>();
        // Pega todos os vértices antecessores até que não
        // haja mais nenhum vértice antecessor.
        while (current != null) {
            path.add(current.getLabel());
            // if (current.getLabel().equals(origin)) {
            //     break;
            // }
            current = current.getPrevious();
        }
        // Inverte a ordem da lista.
        Collections.reverse(path);
        // Retorna a lista invertida.
        return path;
    }

    /**
     * Aplica o algoritmo, de busca de caminho mínima, A*,
     * com o intento de se obter o menor caminho, saindo de
     * uma origem e indo até um objetivo.
     * @param origin O vértice origem, a posição do Ladrão.
     * @param destiny O vértice objetivo.
     * @return O caminho caso seja possível chegar até o 
     * objetivo, ou nulo, caso não tenha solução.
     */
    public ArrayList<String> AStar(String origin, String destiny) {
        // Os vértices descobertos que ainda podem ser expandidos.
        Map<String, Integer> openSet = new HashMap<String, Integer>(){{
            // Inclui o vértice origem já na lista.
            put(origin, 0);
        }};

        // Os vértices que já foram explorados.
        ArrayList<String> closedSet = new ArrayList<String>();

        // O Ladrão já está no destino, ou o Ladrão ao
        // se aproximar do objetivo, percebeu que o 
        // objetivo é impossível de visitar.
        if (origin.equals(destiny) || this.getVertexByLabel(destiny) == null) {
            return null;
        }

        // Itera sobre os vértices descobertos.
        while (!(openSet.isEmpty())) {
            // Pega o vértice com o menor valor.
            Vertex current = this.getVertexByLabel(
                Collections.min(
                    openSet.entrySet(),
                    Map.Entry.comparingByValue()
                ).getKey()
            );

            // Verifica se chegou no objetivo.
            if (current.getLabel().equals(destiny)) {
                return this.reconstructPath(current, origin);
            }

            // Remove o vértice escolhido do "openSet".
            openSet.remove(current.getLabel());

            // Expande os vizinhos.
            for (Vertex neighbor : this.getNeighbors(current.getLabel()).keySet()) {
                if (!(closedSet.contains(neighbor.getLabel()))) {
                    if (!(openSet.containsKey(neighbor.getLabel()))) {
                        // Atualiza o "antecessor", do vértice vizinho
                        // ao vértice atual, no grafo.
                        this.getVertexByLabel(neighbor.getLabel()).setPrevious(current);

                        // Adiciona o vértice vizinho ao "openSet";
                        openSet.put(
                            neighbor.getLabel(), 
                            hScore(neighbor.getLabel(), destiny)
                        );
                    }
                }
            }
            // Adiciona o vértice atual ao "closedSet".
            closedSet.add(current.getLabel());
        }
        // Retorna nulo se não tiver solução.
        return null;
    }
}

/**
 * Representação abstrata
 * de um determinado comportamento.
 */
abstract class Behaviour {
    // O Ladrão herdeiro de tal comportamento.
    protected Ladrao thief;

    /**
     * Adiciona o Ladrão herdeiro de
     * tal comportamento à classe,
     * facilitando as interações com
     * o agente.
     * @param thief O Ladrão atual.
     */
    public Behaviour(Ladrao thief) {
        this.thief = thief;
    }

    /**
     * Realize determinada ação, baseando-se
     * na definição de determinado comportamento.
     * @return A direção a ser seguida pelo Ladrão.
     */
    public abstract int act();
}

/**
 * Representa um comportamento
 * de um "Explorador".
 */
class Intel extends Behaviour {
    /**
     * Construtor base da classe.
     */
    Intel(Ladrao thief) {
        super(thief);
    }

    @Override
    public int act() {
        // Verifica se o Ladrão está no objetivo ou
        // se o Ladrão não tem um objetivo definido.
        if (this.thief.objective == null || this.thief.isThiefOnObjective()) {     
            // Pega as coordenadas do terreno mais distante, 
            // em relação a posição atual do Ladrão e define
            // como novo objetivo.
            this.thief.objective = this.thief.getLongestUnknownVertex();
        }

        // Gera o menor caminho possível para chegar ao objetivo a partir
        // da posição atual do Ladrão.
        int[] currentThiefPosition = this.thief.getThiefCurrentPosition();
        ArrayList<String> pathToObjective = this.thief.graph.AStar(
            this.thief.graph.coordinatesToLabel(
                new int[]{currentThiefPosition[1], currentThiefPosition[0]}
            ), 
            this.thief.objective
        );

        // Caso não exista uma solução para o objetivo 
        // definido pelo Ladrão, um novo objetivo é
        // escolhido.
        if (pathToObjective == null || pathToObjective.size() < 2) {
            this.thief.objective = this.thief.getLongestUnknownVertex();
        } else {
            // O vértice onde o Ladrão está.
            Vertex origin = this.thief.graph.getVertexByLabel(pathToObjective.get(0));
            // O rótulo do vértice para onde o Ladrão tem que ir.
            String destiny = pathToObjective.get(1);

            // Retorna a direção que o Ladrão tem 
            // que seguir para chegar ao objetivo.
            for (Map.Entry<Vertex, Integer> neighbor : this.thief.graph.vertexes.get(origin).entrySet()) {
                // O rótulo do vértice vizinho.
                String neighborLabel = neighbor.getKey().getLabel();
                // A direção do vértice vizinho.
                Integer neighborDir = neighbor.getValue();

                // Verifica se o rótulo vizinho é o
                // caminho a ser seguido.
                if (neighborLabel.equals(destiny)) {
                    // Retorna a direção.
                    return neighborDir;
                }
            }
            // Faz nada.
            return 0;
        }

        // Faz nada.
        return 0;
    }
}

/**
 * Representação de um agente inteligente,
 * do jogo "LadrãoXPoupador", ou seja, o
 * Ladrão.
 */
public class Ladrao extends ProgramaLadrao {
    // A memória do Ladrão, referente ao Labirinto.
    protected int[][] knownField;

    // O grafo relacionado à memória do Ladrão.
    protected Graph graph;

    // O objetivo do Ladrão, onde ele quer chegar.
    protected String objective;
    
    // O comportamento do Ladrão, por padrão
    // todos os agentes começam como exploradores
    // e ao longo do tempo, eles decidirão se 
    // mudarão de comportamento ou não.
    protected Behaviour behaviour = new Intel(this);

    // Os terrenos impossíveis de visitar.
    protected ArrayList<Integer> nonVisitableLands = new ArrayList<>(
       Arrays.asList(
            -1,  // Sem visão para o terreno.
            1,   // Parede.
            3,   // Banco.
            4,   // Moeda.
            5,   // Pastilha do Poder.
            200, // O Ladrão 1.
            210, // O Ladrão 2.
            220, // O Ladrão 3.
            230  // O Ladrão 4.
        )
    );

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
    }

    /**
     * Faz com que o Ladrão verifica se determinado
     * terreno, a partir de sua memória/visão, é 
     * visitável ou não.
     * @param landContent O conteúdo de um terreno.
     * @return Indica se o terreno é visitável.
     */
    protected boolean isLandInvalid(int landContent) {
        return nonVisitableLands.contains(landContent);
    }

    /**
     * Calcula a distância entre todos os vértices
     * desconhecidos do grafo com o vértice origem.
     * @return O vértice desconhecido mais distante
     * em relação a posição do Ladrão.
     */
    protected String getLongestUnknownVertex() {
        // Armazena os vértices do grafo e as distâncias.
        Map<String, Integer> unknownVerticesDistances = new HashMap<>();

        // A posição atual do Ladrão.
        int[] currentThiefPosition = this.getThiefCurrentPosition();
        // O rótulo de um vértice com a posição atual do Ladrão.
        String root = this.graph.coordinatesToLabel(
            new int[]{currentThiefPosition[1], currentThiefPosition[0]}
        );

        // Itera sobre todos os vértices do grafo.
        for (Vertex vertex : this.graph.vertexes.keySet()) {
            // Pega o conteúdo do vértice.
            int[] vertexCoordinates = this.graph.labelToCoordinates(vertex.getLabel());
            Integer landContent = this.knownField[vertexCoordinates[0]][vertexCoordinates[1]];
            // Verifica se o terreno é desconhecido.
            if (landContent.equals(-2)) {
                // Adiciona o vértice e a distância ao dicionário.
                unknownVerticesDistances.put(
                    vertex.getLabel(),
                    this.graph.hScore(root, vertex.getLabel())
                );
            }
        }
        // Retorna o rótulo do vértice mais distante.
        return Collections.max(
            unknownVerticesDistances.entrySet(),
            Map.Entry.comparingByValue()
        ).getKey();
    }

    /**
     * Verifica se o Ladrão está no objetivo,
     * verificando se a sua posição é igual
     * a do objetivo.
     * @return Se o Ladrão está no objetivo.
     */
    protected boolean isThiefOnObjective() {
        // A posição atual do Ladrão como rótulo de um vértice.
        String thiefCurrentPosition = this.graph.coordinatesToLabel(
            this.getThiefCurrentPosition()
        );
        // Se o Ladrão está no objetivo.
        return thiefCurrentPosition.equals(this.objective);
    }

    /**
     * Retorna uma lista de inteiros, contendo
     * o 'x' e o 'y' do Ladrão.
     * @return Uma lista de inteiros, refente à
     * posição do Ladrão.
     */
    protected int[] getThiefCurrentPosition() {
        // Pega a posição atual do Ladrão.
        java.awt.Point currentPosition = this.sensor.getPosicao();
        // Converte ambos os valores ('x' e 'y') para inteiro.
        Integer x = (int) currentPosition.getX();
        Integer y = (int) currentPosition.getY();
        // Retorna uma lista de inteiros, contendo ambas as coordenadas.
        return new int[]{x, y};
    }

    /**
     * Pega os terrenos adjacentes, contendo, também,
     * seus valores de direção, de um terreno fornecido 
     * qualquer.
     * @param x A coordenada 'x' do terreno fornecido.
     * @param y A coordenada 'y' do terreno fornecido.
     * @return Um dicionário, contendo os terrenos adjacentes
     * que são visitáveis e seus valores de direção.
     */
    private Map<String, Integer> getAdjacentLands(int x, int y) {
        // Os valores que, se somados a posição do terreno
        // fornecido, isto é, o 'x' e o 'y', retornarão os
        // terrenos adjacentes.
        int[] adjacentLandsIndex = new int[]{0, -1, 0, 1, 1, 0, -1, 0};

        // Armazenará os terrenos adjacentes.
        Map<String, Integer> adjacentLands = new HashMap<>();

        // A direção referente ao valores somados às coordenadas
        // fornecidas, a ordem seguida é : 4, 3, 2, 1.
        Integer landTravelDirection = 4;

        // Itera sobre pares de índices dos terrenos adjacentes.
        for (int adjI = 0; adjI < adjacentLandsIndex.length; adjI += 2) {
            // As coordenadas do terreno adjacente ao terreno atual.
            Integer adjacentLandX = adjacentLandsIndex[adjI] + x;
            Integer adjacentLandY = adjacentLandsIndex[adjI + 1] + y;
            
            // Valida se as coordenadas do terreno adjacente estão dentro do
            // labirinto.
            if (0 <= adjacentLandX && adjacentLandX <= 29) {
                if (0 <= adjacentLandY && adjacentLandY <= 29) {
                    // Pega o conteúdo do terreno adjacente.
                    Integer adjacentLandContent = this.knownField[adjacentLandX][adjacentLandY];
                    // Verifica se o terreno é visitável.
                    if (!(this.isLandInvalid(adjacentLandContent))) {
                        // Adiciona aos terrenos adjacentes.
                        adjacentLands.put(
                            this.graph.coordinatesToLabel(
                                new int[]{adjacentLandX, adjacentLandY}
                            ), landTravelDirection
                        );
                    }
                }
            }
            // Altera o valor de direção do terreno.
            landTravelDirection--;
        }
        // Retorna os terrenos adjacentes.
        return adjacentLands;
    }

    /**
     * Atualiza o grafo, referente à memória do Ladrão
     * sobre o Labirinto, sempre que a memória do Ladrão
     * for alterada.
     */
    private void updateGraphBasedOnMemory() {
        // Reseta o grafo do Ladrão.
        this.graph = new Graph();

        // Percorre todos os terrenos do labirinto,
        // a partir da memória do Ladrão.
        for (int i = 0; i < this.knownField.length; i++) {
            for (int j = 0; j < this.knownField[i].length; j++) {
                // Pega o conteúdo do terreno, ou seja,
                // o que o Ladrão se lembra.
                Integer landContent = this.knownField[i][j];

                // Ignora os terrenos impossíves de visitar.
                if (!this.isLandInvalid(landContent)) {
                    // Cria um vértice, do terreno atual, para o grafo,
                    // caso ainda não exista no grafo.
                    String currentLand = this.graph.coordinatesToLabel(new int[]{i, j});
                    this.graph.addVertex(currentLand);

                    // Pega os terrenos adjacentes ao terreno atual.
                    for (Map.Entry<String, Integer> entry : this.getAdjacentLands(i, j).entrySet()) {
                        // O ŕotulo do terreno adjacente.
                        String adjVertex = entry.getKey();
                        // O valor de direção do terreno adjacente.
                        Integer adjVertexDirection = entry.getValue();

                        // Adicion o vértice ao grafo, caso ainda não exista
                        // no grafo.
                        this.graph.addVertex(adjVertex);

                        // Adiciona uma aresta entre o terreno atual
                        // e o terreno adjacente.
                        this.graph.addEdge(currentLand, adjVertex, adjVertexDirection);
                    }
                }
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
        Integer thiefX = positions[0];
        Integer thiefY = positions[1];

        // A visão atual do Ladrão.
        int[] currentView = this.sensor.getVisaoIdentificacao();

        // O índice dos terrenos na matriz de visão do Ladrão.
        Integer gridViewIndex = 0;

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

    public static ArrayList<ArrayList<Integer>> criaMatriz(){
        ArrayList<ArrayList<Integer>> matrizPassos = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < 30; i++) {
            ArrayList<Integer> array = new ArrayList<Integer>(30);
            for (int j = 0; j < 30; j++) {
                array.add(0);
            }
            matrizPassos.add(array);
        }
        return matrizPassos;
    }

    public void printaMatriz(ArrayList<ArrayList<Integer>> matriz) {
        for (int i = 0; i < matriz.size(); i++) {
            for (int j = 0; j < matriz.get(i).size(); j++) {
                System.out.print(matriz.get(i).get(j));
            }
            System.out.println();
        }
    }

    public void setMigalha(int x, int y){
        // ArrayList<ArrayList<Integer>> m = matriz;
        int valorAnterior = matrixFinal.get(x).get(y);
        // printaMatriz(matrixFinal);
        // System.out.println(
        //     "---------------------------------------------------------------------"
        // );
        matrixFinal.get(x).set(y, valorAnterior+1);
        // printaMatriz(matrixFinal);
        // System.out.println(
        //     "---------------------------------------------------------------------"
        // );
        // return m;   
    }
    
    public boolean estaLivre(int position) {
        int[] visao = this.sensor.getVisaoIdentificacao();
        
        return visao[position] == 0;
    }

    static ArrayList<ArrayList<Integer>> matrixFinal = criaMatriz();

    public int migalhas(){
        
        int[] position = this.getThiefCurrentPosition();
        //ArrayList<ArrayList<Integer>> matriz = setMigalha(position[0], position[1], matrix);
        setMigalha(position[0], position[1]);
        int cima = 7;
        int baixo = 16;
        int direita = 12;
        int esquerda = 11;
        int migalhaValorD = Integer.MAX_VALUE;
        int migalhaValorE = Integer.MAX_VALUE;
        int migalhaValorC = Integer.MAX_VALUE;
        int migalhaValorB = Integer.MAX_VALUE;

        if (estaLivre(direita) ) {
            migalhaValorD = matrixFinal.get(position[0] + 1).get(position[1]);
        }
        if (estaLivre(esquerda)) {
            migalhaValorE = matrixFinal.get(position[0] - 1).get(position[1]);
        }
        if (estaLivre(cima)){
            migalhaValorC = matrixFinal.get(position[0]).get(position[1] - 1);
        }
        if (estaLivre(baixo)){
            migalhaValorB = matrixFinal.get(position[0]).get(position[1] + 1);
        }
        
   
        
        float r = (float) Math.random() * 4;
        int rand = Math.round(r);
        
        if (migalhaValorC < migalhaValorB && migalhaValorC < migalhaValorD && migalhaValorC < migalhaValorE) {
                    
            // printaMatriz(matriz);
            // System.out.println(
            //     "---------------------------------------------------------------------"
            // );
            return 1;
        } else if (migalhaValorB < migalhaValorC && migalhaValorB < migalhaValorD && migalhaValorB < migalhaValorE) {
                    
            // printaMatriz(matriz);
            // System.out.println(
            //     "---------------------------------------------------------------------"
            // );
            return 2;
        } else if (migalhaValorE < migalhaValorC && migalhaValorE < migalhaValorD && migalhaValorE < migalhaValorB) {
                        
            // printaMatriz(matriz);
            // System.out.println(
            //     "---------------------------------------------------------------------"
            // );
            return 4;
        } else if (migalhaValorD < migalhaValorC && migalhaValorD < migalhaValorE && migalhaValorD < migalhaValorB) {
                    
            // printaMatriz(matriz);
            // System.out.println(
            //     "---------------------------------------------------------------------"
            // );
            return 3;
        } else {
                    
            // printaMatriz(matriz);
            // System.out.println(
            //     "---------------------------------------------------------------------"
            // );
            return rand;
        }
        


    }

    @Override
    public int acao() {
        // this.memorizeVisitedLands();
        // this.updateGraphBasedOnMemory();
        // ArrayList<ArrayList<Integer>> matriz = new ArrayList<ArrayList<Integer>>();
        // int ctd = 0;
        // if (ctd == 0) {
        //     matriz = criaMatriz();
        //     ctd++;
        // }

        return this.migalhas();
    }
}
