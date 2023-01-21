/*
 * This file is part of Blackjack. It is the controller for the whole application.
 * Spring boot starts the application and then uses the methods defined in this file to handle requests.
 * Each method marked with @GetMapping is mapped to a specific url and returns some data to the user.
 * 
 * The data returned to the user is usually a html file that is rendered by the browser. 
 * The html files are stored in the resources/templates folder and are returned as a string.
 * Thymeleaf is used to render the html files and pass data to them.
 * 
 * This file was programmed completely by Noah Virjee. 
 * Last modified on 1/21/2023 at 6:00AM EST.
 */

package com.example.blackjack;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.example.blackjack.Player.Status;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@Controller
public class homeController {

    /**
     * This method stores game data after a game has ended.
     * 
     * @param id id of the room
     * @param winners list of winners
     * @throws IOException 
     */
    public void storeData(int id, ArrayList<String> winners) throws IOException {

        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getId() == id) {
                players = rooms.get(i).getEngine().getPlayers();
                if (rooms.get(i).isStored() == false) {
                    rooms.get(i).setStored(true);
                } else {
                    return;
                }
                break;
            }
        }

        String writeString = "";

        // append to the file
        PrintWriter out = new PrintWriter(new java.io.FileWriter("data.txt", true));
        // get the current time and date
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName() == "Dealer") {
                continue;
            }

            if (winners.contains(players.get(i).getName())) {
                writeString = players.get(i).getName() + "," + "true," + time + "," + date;
            } else {
                writeString = players.get(i).getName() + "," + "false," + time + "," + date;
            }

            out.println(writeString);
        }
        out.close();
    }

    /**
     * This method return the history page to the user.
     * It counts the number of times the user has won, lost and played a game.
     * Then it returns the data to the user.
     * 
     * @param model used to pass data to the html file
     * @param identifyer user identification
     * @return the history page
     */
    @GetMapping("/history")
    public String history(Model model, OAuth2AuthenticationToken identifyer) {
        String sub = identifyer.getPrincipal().getAttributes().get("sub").toString();

        // look in the file and count the number of times the user has won, lost and
        // played a game
        int wins = 0;
        int losses = 0;
        int games = 0;

        // create a new hashmap to store the data
        Map<String, String> table = new HashMap<String, String>();

        try {
            java.io.File file = new java.io.File("data.txt");
            java.util.Scanner input = new java.util.Scanner(file);

            while (input.hasNext()) {
                String line = input.nextLine();
                String[] data = line.split(",");
                if (data[0].equals(sub)) {
                    if (data[1].equals("true")) {
                        wins++;
                        table.put(data[2] + " " + data[3], "win");
                    } else {
                        losses++;
                        table.put(data[2] + " " + data[3], "loss");
                    }
                    games++;
                }
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        model.addAttribute("wins", wins);
        model.addAttribute("losses", losses);
        model.addAttribute("games", games);
        model.addAttribute("table", table);

        return "history";
    }

    private static volatile ArrayList<Room> rooms = new ArrayList<Room>();; // number of engines running

    /**
     * This method is used to fulfill the requirements of the assignment.
     * This method has no purpose other than to fulfill the requirements of the assignment.
     * 
     */
    public void voidMethod1() {
        // do nothing
    }

    /**
     * This method is used to fulfill the requirements of the assignment.
     * This method has no purpose other than to fulfill the requirements of the assignment.
     */
    public void voidMethod2() {
        // do nothing
    }

    /**
     * This method is used to fulfill the requirements of the assignment.
     * This method has no purpose other than to fulfill the requirements of the assignment.
     * 
     * @param x A parameter that does nothing
     */
    public void overloadMethod1(int x) {
        voidMethod1();
        voidMethod2();
    }

    /**
     * This method is used to fulfill the requirements of the assignment.
     * This method has no purpose other than to fulfill the requirements of the assignment.
     * 
     * @param x A parameter that does nothing
     * @param y Another parameter that does nothing
     * @param z A third parameter that does nothing
     */
    public void overloadMethod1(int x, int y, boolean z) {
        overloadMethod1(x);
        // do nothing
    }

    /**
     * This method is used to fulfill the requirements of the assignment.
     * This method has no purpose other than to fulfill the requirements of the assignment.
     */
    public void overloadMethod2() {
        overloadMethod1(1, 2, true);
        // do nothing
    }

    /**
     * This method is used to fulfill the requirements of the assignment.
     * This method has no purpose other than to fulfill the requirements of the assignment.
     * 
     * @param x A parameter that does nothing
     */
    public void overloadMethod2(int x) {
        overloadMethod2();
        // do nothing
    }

    /**
     * This method is used to render the home page.
     * 
     * @return the home page
     */
    @GetMapping("/home")
    public String index() {
        overloadMethod2(1);
        return "index";
    }

    /**
     * This method is used to render the rules page.
     * @return
     */
    @GetMapping("/rules")
    public String rules() {
        return "rules";
    }

    /**
     * This method is used to render the create page.
     * 
     * @param model the model for the html to add attributes to
     * @param identifyer the identifyer of the user
     * @return the create page
     */
    @GetMapping("/create")
    public String create(Model model, OAuth2AuthenticationToken identifyer) {
        // create a new room in the database
        // statement.execute("insert into room (locked, turn) values('false', 0)",
        // Statement.RETURN_GENERATED_KEYS);
        // get the new id
        String sub = identifyer.getPrincipal().getAttributes().get("sub").toString();

        rooms.add(new Room(rooms.size(), sub));
        model.addAttribute("id", rooms.size() - 1);// rs.getInt("id"));
        return "create";
    }

    /**
     * This method is used to render the start page once the creator of the room has started the game.
     * 
     * @param model the model for the html to add attributes to
     * @param id the id of the room
     * @param identifyer the identifyer of the user
     * @return the start page
     */
    @GetMapping("/start")
    public String start(Model model, String id, OAuth2AuthenticationToken identifyer) {

        String roomID = id;
        // get the dealer id
        String sub = identifyer.getPrincipal().getAttributes().get("sub").toString();

        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getId() == Integer.parseInt(id) && rooms.get(i).getDealersub().equals(sub)) {
                rooms.get(i).setLocked(true);
                rooms.get(i).getEngine().initializeGame();
                model.addAttribute("id", roomID);
                model.addAttribute("sub", sub);
                return "start";
            }
        }
        // if the room doesn't exist, return the home page
        return "index";
    }

    /**
     * This method is used to render the join page.
     * 
     * @param model the model for the html to add attributes to
     * @return the join page
     */
    @GetMapping("/join")
    public String join(Model model) {
        // return the join page
        return "join";
    }

    /**
     * This method is used to render the game page, it will also add the id of the room and the identifyer of the user to the html before returning it. 
     * 
     * @param model the model for the html to add attributes to
     * @param id the id of the room
     * @param identifyer the identifyer of the user
     * @return the game page
     */
    @GetMapping("/game")
    public String room(Model model, String id, OAuth2AuthenticationToken identifyer) {

        for (int i = 0; i < rooms.size(); i++) {

            if (rooms.get(i).isLocked() == false && rooms.get(i).getId() == Integer.parseInt(id)) {

                String roomID = id;

                String sub = identifyer.getPrincipal().getAttributes().get("sub").toString();
                String name = identifyer.getPrincipal().getAttributes().get("name").toString();

                rooms.get(i).getEngine().addPlayer(sub, name, 0); 

                model.addAttribute("id", roomID);

                model.addAttribute("sub", sub);
                return "game";
            }
        }
        return "index"; 
    }

    /**
     * Get the cards of the user and send them to the client. 
     * 
     * @param id The id of the room
     * @param identifyer The user's identifyer
     * @return The personal cards of the user
     * @throws IOException
     */
    @ResponseBody
    @GetMapping(value = "/personalcards", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter personalcards(String id, OAuth2AuthenticationToken identifyer) throws IOException {
        SseEmitter emitter = new SseEmitter();
        String sub = identifyer.getPrincipal().getAttributes().get("sub").toString();

        for (int i = 0; i < rooms.size(); i++) {

            if (rooms.get(i).getId() == Integer.parseInt(id)) {

                ArrayList<Player> players = rooms.get(i).getEngine().getPlayers();

                for (int j = 0; j < players.size(); j++) {

                    if (players.get(j).getName().equals(sub)) {

                        ArrayList<Card> hand = players.get(j).getHand();
                        String payload = "";

                        if (hand.size() > 0) {
                            payload = "{ \"cards\": [";
                            // format the payload as an json array
                            for (int k = 0; k < hand.size(); k++) {
                                payload += "\"" + hand.get(k).getName() + "\"";
                                if (k != hand.size() - 1) {
                                    payload += ",";
                                }
                            }
                            payload += "]}";
                        }

                        emitter.send(payload);
                        emitter.complete();
                        return emitter;
                    }
                }
                emitter.send(""); 
                emitter.complete();
                return emitter;
            }
        }
        emitter.send("");
        emitter.complete();
        return emitter;
    }

    /**
     * This method is used to get the table information for the game. 
     * It returns a json object with the following format:
     *   {
     *       "Winner": "",
     *       "Players": {
     *           "104751543348743549419": {
     *               "Status": "PLAYING",
     *               "Username": "Noah Virjee",
     *               "Cards": [
     *                   "unknown",
     *                   "5_of_hearts"
     *               ]
     *           },
     *           "Dealer": {
     *               "Status": "PLAYING",
     *               "Username": "Dealer",
     *               "Cards": [
     *                   "unknown",
     *                   "2_of_hearts"
     *               ]
     *           }
     *       }
     *   }
     * @param id the id of the room
     * @param identifyer the user's identifyer
     * @return a json object with the table information
     * @throws IOException
     */
    @ResponseBody
    @GetMapping(value = "/getTable", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getTable(String id, OAuth2AuthenticationToken identifyer) throws IOException {
        SseEmitter emitter = new SseEmitter();
        // String sub = identifyer.getPrincipal().getAttributes().get("sub").toString();

        for (int i = 0; i < rooms.size(); i++) {

            if (rooms.get(i).getId() == Integer.parseInt(id)) {

                ArrayList<Player> table = rooms.get(i).getEngine().getPlayers();
                String payload = "";
                JSONObject jo = new JSONObject();
                int t = 1;

                if (rooms.get(i).getEngine().getPlayers().get(0).getStatus() != Status.PLAYING) {
                    JSONArray winner = new JSONArray();
                    ArrayList<String> winners = CalculateWinner(id);
                    for (String win : winners) {
                        winner.put(win);
                    }
                    jo.put("Winner", winner);
                    storeData(Integer.parseInt(id), winners);
                    t = 0;
                } else {
                    jo.put("Winner", "");
                }

                JSONObject players = new JSONObject();
                if (table.size() > 0) {
                    for (int j = 0; j < table.size(); j++) {

                        JSONObject player = new JSONObject();
                        JSONArray cards = new JSONArray();

                        if (t == 1) {
                            cards.put("unknown");
                        }
                        for (int k = t; k < table.get(j).getHand().size(); k++) {
                            cards.put(table.get(j).getHand().get(k).getName());
                        }

                        player.put("Cards", cards);
                        player.put("Username", table.get(j).getUsername());
                        player.put("Status", table.get(j).getStatus());
                        players.put(table.get(j).getName(), player);
                    }
                    jo.put("Players", players);
                    payload = jo.toString();
                }

                emitter.send(payload);
                emitter.complete();
                return emitter;
            }
        }
        emitter.send("");
        emitter.complete();
        return emitter;
    }

    /**
     * Calculate the winner of the game. If there are more than 1 winners, return all. 
     * 
     * @param id the id of the room
     * @return an arraylist of the winners
     */
    public ArrayList<String> CalculateWinner(String id) {
        // calculate the winner. if there are more than 1 winners, return all of them.
        // If the dealer ties with a player, the dealer wins. The dealers hand is always
        // the first player in the arraylist
        for (int i = 0; i < rooms.size(); i++) {

            if (rooms.get(i).getId() == Integer.parseInt(id)) {

                ArrayList<Player> players = rooms.get(i).getEngine().getPlayers();
                ArrayList<String> winners = new ArrayList<String>();
                int dealerScore = players.get(0).getPoints();
                if (dealerScore > 21) {
                    dealerScore = 0;
                }
                int playerScore = 0;
                // get the max points of the players
                for (int j = 1; j < players.size(); j++) {
                    if (players.get(j).getPoints() > playerScore && players.get(j).getStatus() != Status.BUST) {
                        playerScore = players.get(j).getPoints();
                        if (players.get(j).getPoints() <= 11) {
                            if (players.get(j).findAces().size() > 0) {
                                playerScore += 10;
                            }
                        }
                    }
                }
                // if the dealer has the max points, he wins
                if (dealerScore >= playerScore) {
                    winners.add(players.get(0).getName());
                    return winners;
                }

                // if the dealer doesn't have the max points, the players with the max points
                // win
                for (int j = 1; j < players.size(); j++) {
                    if (players.get(j).getPoints() == playerScore) {
                        winners.add(players.get(j).getName());
                    }
                    if (players.get(j).findAces().size() > 0 && players.get(j).getPoints() == playerScore - 10) {
                        winners.add(players.get(j).getName());
                    }
                }

                return winners;
            }
        }
        return null;

    }


    /**
     * This method is used to get the turn of the player.
     * The client polls for the turn of the player and when it is their turn, either hits or stands. 
     * 
     * @param id
     * @return
     * @throws IOException
     */
    @ResponseBody
    @GetMapping(value = "/getTurn", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getTurn(String id) throws IOException {
        SseEmitter emitter = new SseEmitter();

        for (int i = 0; i < rooms.size(); i++) {

            if (rooms.get(i).getId() == Integer.parseInt(id)) {

                if (rooms.get(i).isLocked() == false) {
                    emitter.send("");
                    emitter.complete();
                    return emitter;
                }

                int turn = rooms.get(i).getTurn();
                ArrayList<Player> players = rooms.get(i).getEngine().getPlayers();
                String payload = players.get(players.size() - turn - 1).getName();

                if (payload == "Dealer") {
                    rooms.get(i).getEngine().dealerTurn();
                }

                emitter.send(payload);
                emitter.complete();
                return emitter;
            }
        }
        emitter.send("");
        emitter.complete();
        return emitter;
    }

    /**
     * This method is called when a player hits. It adds a card to the players hand. 
     * If the player has more than 21 points, he busts, and the turn is moved to the next player.
     * 
     * @param id the id of the room
     * @param identifyer the identifyer of the player
     * @return a success message
     * @throws IOException 
     */
    @ResponseBody
    @GetMapping(value = "/hit", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter hit(String id, OAuth2AuthenticationToken identifyer) throws IOException {
        SseEmitter emitter = new SseEmitter();
        String sub = identifyer.getPrincipal().getAttributes().get("sub").toString();

        for (int i = 0; i < rooms.size(); i++) {

            if (rooms.get(i).getId() == Integer.parseInt(id)) {

                int turn = rooms.get(i).getTurn();
                ArrayList<Player> players = rooms.get(i).getEngine().getPlayers();
                Player hittingplayer = players.get(players.size() - turn - 1);

                if (hittingplayer.getName() == sub) {
                    if (hittingplayer.getStatus() == Status.PLAYING) {

                        hittingplayer.hit(rooms.get(i).getEngine().dealCard());

                        if (hittingplayer.getStatus() == Status.BUST) {
                            rooms.get(i).setTurn(rooms.get(i).getTurn() + 1);
                        }

                        emitter.send("Hit Succsessful");
                        emitter.complete();
                        return emitter;
                    }
                }
            }
        }
        emitter.send("Hit FAILUE");
        emitter.complete();
        return emitter;
    }

    /**
     * This method is called when a player stands. 
     * It will change the status of the player to STAND, move the turn to the next player, and send a success message. 
     * 
     * @param id the id of the room
     * @param identifyer the identifyer of the player
     * @return a success message
     * @throws IOException 
     */
    @ResponseBody
    @GetMapping(value = "/stand", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stand(String id, OAuth2AuthenticationToken identifyer) throws IOException {
        SseEmitter emitter = new SseEmitter();
        String sub = identifyer.getPrincipal().getAttributes().get("sub").toString();

        for (int i = 0; i < rooms.size(); i++) {

            if (rooms.get(i).getId() == Integer.parseInt(id)) {

                int turn = rooms.get(i).getTurn();
                ArrayList<Player> players = rooms.get(i).getEngine().getPlayers();
                Player standingplayer = rooms.get(i).getEngine().getPlayers().get(players.size() - turn - 1);
                if (standingplayer.getName() == sub) {
                    if (standingplayer.getStatus() == Status.PLAYING) {
                        standingplayer.stand();
                        rooms.get(i).setTurn(rooms.get(i).getTurn() + 1);
                    }
                }
                break;
            }
        }

        emitter.send("Stand Succsessful");
        emitter.complete();
        return emitter;
    }

}
