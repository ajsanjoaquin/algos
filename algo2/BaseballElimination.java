import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.Bag;

public class BaseballElimination {
    private final int numTeams;
    // {team name: Integer}
    private ST<String, Integer> teamList = new ST<>();
    // {team index: int array of [0] = wins, [1] = losses, [2] = remaining games}
    private ST<Integer, int[]> teamTable  = new ST<>();;
    private int[][] againstTable;
    private FlowNetwork network;

    private ST<String, Bag<String>> eliminatedList = new ST<>();

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In file = new In(filename);
        numTeams = file.readInt();
        againstTable = new int[numberOfTeams()][numberOfTeams()];
        int index = 0;
        while(!file.isEmpty()) {
            String teamName = file.readString();
            teamList.put(teamName, index);
            int[] intList = new int[3];
            intList[0] = file.readInt();
            intList[1] = file.readInt();
            intList[2] = file.readInt();
            teamTable.put(index, intList);
            assert teamList.get(teamName) == index;

            for (int col = 0; col < numberOfTeams(); col++) {
                againstTable[index][col] = file.readInt();;
            }
            index++;
        }
        file.close();
    }             

    // number of teams    
    public int numberOfTeams() {
        return numTeams;
    }       
    
    // all teams
    public Iterable<String> teams() {
        return teamList;
    }       
    private void checkValidTeam(String team) {
        if (!teamList.contains(team)) throw new java.lang.IllegalArgumentException();
    }
    // number of wins for given team
    public int wins(String team) {
        checkValidTeam(team);
        return teamTable.get(teamList.get(team))[0];
    }                    

    // number of losses for given team  
    public int losses(String team) {
        checkValidTeam(team);
        return teamTable.get(teamList.get(team))[1];
    }                    

    // number of remaining games for given team
    public int remaining(String team) {
        checkValidTeam(team);
        return teamTable.get(teamList.get(team))[2];
    }            

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!teamList.contains(team1) || !teamList.contains(team2)) throw new java.lang.IllegalArgumentException();
        return againstTable[teamList.get(team1)][teamList.get(team2)];
    } 

    // is given team eliminated?
    public boolean isEliminated(String team) {
        checkValidTeam(team);
        if (trivialElim(team)) return true;
        createNetwork(team);
        //StdOut.print(network.toString());
        // compute maxflow solution using Ford-Fulkerson Algorithm
        FordFulkerson sol = new FordFulkerson(network, 0, network.V()-1); // manipulates original object
        //StdOut.print(network.toString());

        // a team is only eliminated iff its game vertices are not full
        for (FlowEdge edge : network.adj(0))
            if (edge.flow() != edge.capacity()) {
                certify(network, sol, team);
                return true; 
            } 
        return false;
    }           

    private boolean trivialElim (String team) {
        boolean eliminated = false;
        int max = 0;
        String highestTeam = null;
        for (String opposing : teams())
            if (opposing != team && (wins(team) + remaining(team) < wins(opposing))) {
                if (max < wins(opposing)) {
                    max = wins(opposing);
                    highestTeam = opposing;
                }
                eliminated =  true;
            }
        if (highestTeam != null) {
            Bag<String> subset = new Bag<>();
            subset.add(highestTeam);
            eliminatedList.put(team, subset);
        }
        return eliminated;
    }
    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        checkValidTeam(team);
        if (!isEliminated(team)) return null;
        return eliminatedList.get(team);
    }

    private void createNetwork(String team) {
        // formula for n choose 2 
        int gameVertices = numberOfTeams() * (numberOfTeams() - 1) / 2;
        network = new FlowNetwork(gameVertices + numberOfTeams() + 2);
        int source = 0;
        int sink = network.V()-1;
        int gameVertex = 1;
        for (int row = 0; row < numberOfTeams(); row ++) {
            // col is always at least 1 higher than row because they can never be the same 
            // (For game vertex x vs. y, x and y must be distinct)
            for (int col = row + 1; col < numberOfTeams(); col++) {
                // s to game vertices
                network.addEdge(new FlowEdge(source, gameVertex, againstTable[row][col]));
                // each game vertex to two team vertices
                network.addEdge(new FlowEdge(gameVertex, gameVertices + row + 1, Double.POSITIVE_INFINITY));
                network.addEdge(new FlowEdge(gameVertex, gameVertices + col + 1, Double.POSITIVE_INFINITY));
                gameVertex++;
            }
            //assert gameVertex == gameVertices;
            // team vertex to sink
            // using a teamVertexCount instead of row to avoid skipping
            int teamVertex = (gameVertices + row) + 1;
            // capacity = wins[arg_team] + remaining[arg_team] - wins[team vertex]
            network.addEdge(new FlowEdge(teamVertex, sink, wins(team) + remaining(team) - teamTable.get(row)[0]));
        }
    }

    private void certify(FlowNetwork network, FordFulkerson sol, String team) {
        // subset of other teams in the division where the selected team is mathematically eliminated
        Bag<String> subset = new Bag<>();
        int gameVertices = (numberOfTeams()-1) * (numberOfTeams() - 2) / 2;
        boolean encounteredTeam = false;   
        for (String opposing : teams()) {
            int teamIndex = teamList.get(opposing);
            if (opposing == team) {
                encounteredTeam = true;
                continue;
            }
            if (encounteredTeam) teamIndex--; // decrease the index to skip queried team
            // the team vertex corresponding to the given opposing team
            int teamVertex = (gameVertices + teamIndex) + 1;
            // check if the team is within the mincut
            if (sol.inCut(teamVertex)) subset.add(opposing);
        }
        eliminatedList.put(team, subset);
    }
    
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}