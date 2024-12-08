import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = Integer.parseInt(sc.nextLine());
        List<String> commands = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            commands.add(sc.nextLine().trim());
        }
        processCommands(commands);
    }

    private static void processCommands(List<String> commands) {
        Stack<LoopContext> stack = new Stack<>();
        StringBuilder output = new StringBuilder();
        int index = 0;

        while (index < commands.size()) {
            String command = commands.get(index);

            if (command.startsWith("for")) {
                int times = Integer.parseInt(command.split(" ")[1]);
                stack.push(new LoopContext(times));
            } else if (command.equals("do")) {
                // No operation for "do"
            } else if (command.equals("done")) {
                LoopContext currentLoop = stack.pop();
                currentLoop.incrementIteration();

                if (currentLoop.hasMoreIterations()) {
                    stack.push(currentLoop);
                    index = findLoopStart(commands, index);
                    continue;
                }
            } else if (command.startsWith("break")) {
                int breakAt = Integer.parseInt(command.split(" ")[1]);
                if (stack.peek().currentIteration + 1 == breakAt) {
                    stack.pop();
                    index = findLoopEnd(commands, index);
                    continue;
                }
            } else if (command.startsWith("continue")) {
                int continueAt = Integer.parseInt(command.split(" ")[1]);
                if (stack.peek().currentIteration + 1 == continueAt) {
                    stack.peek().incrementIteration();
                    if (stack.peek().hasMoreIterations()) {
                        index = findLoopStart(commands, index);
                    } else {
                        stack.pop();
                    }
                    continue;
                }
            } else if (command.startsWith("print")) {
                String message = command.substring(command.indexOf("\"") + 1, command.lastIndexOf("\""));
                output.append(message).append("\n");
            }

            index++;
        }

        System.out.print(output.toString());
    }

    private static int findLoopStart(List<String> commands, int currentIndex) {
        int nestedLoops = 0;
        for (int i = currentIndex - 1; i >= 0; i--) {
            if (commands.get(i).equals("done")) {
                nestedLoops++;
            } else if (commands.get(i).equals("do")) {
                if (nestedLoops == 0) {
                    return i;
                }
                nestedLoops--;
            }
        }
        return 0; // This should not happen in a well-formed input
    }

    private static int findLoopEnd(List<String> commands, int currentIndex) {
        int nestedLoops = 0;
        for (int i = currentIndex + 1; i < commands.size(); i++) {
            if (commands.get(i).equals("do")) {
                nestedLoops++;
            } else if (commands.get(i).equals("done")) {
                if (nestedLoops == 0) {
                    return i;
                }
                nestedLoops--;
            }
        }
        return commands.size(); // This should not happen in a well-formed input
    }

    // Helper class to represent the context of a loop
    static class LoopContext {
        int totalIterations;
        int currentIteration;

        LoopContext(int totalIterations) {
            this.totalIterations = totalIterations;
            this.currentIteration = 0;
        }

        void incrementIteration() {
            currentIteration++;
        }

        boolean hasMoreIterations() {
            return currentIteration < totalIterations;
        }
    }
}
