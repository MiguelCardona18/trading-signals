import java.lang.Math;
import java.util.*; 
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class GA_task1 {

    // Training
    public static int ROW_SIZE=1291; //no. training patterns 
    public static int COLUMN_SIZE=5; //no. row patterns (PRICE, [4 signals])
    public static double[][] train = new double [ROW_SIZE][COLUMN_SIZE]; //data to train 

    // Individual representation
    public static int POPULATION_SIZE = 100; //no. of individuals
    public static int FEATURE_SIZE = 4; //weight of each indicator
    public static double[][] pop = new double[POPULATION_SIZE][FEATURE_SIZE]; //population representation

    // Fitness
    public static int OPTIONS = 3; //i.e buy, sell, and hold
    public static double[] decisions = new double[OPTIONS];
    public static double[] budget_fitness = new double[POPULATION_SIZE]; //end cash balance


    public static void main(String[] args) throws IOException {
        Load_Data(ROW_SIZE, COLUMN_SIZE); //load data - filling train
        generate_population(POPULATION_SIZE, FEATURE_SIZE); //fill in pop

        generate_fitness(pop, train);
        System.out.println("\nBest fitness: " + Math.round(budget_fitness[max_fitness(budget_fitness)] * 100.0) / 100.0);


        for(int i = 0; i<1000; i++){ //1000 generations 
            GA(); //acts on pop

            generate_fitness(pop, train);
            System.out.println("\nBest fitness: " + Math.round(budget_fitness[max_fitness(budget_fitness)] * 100.0) / 100.0);
        }
        
    }


    public static int max_fitness(double[] f) {
        double max_fitness = f[0];
        int best_position = 0;   
        for (int i=1; i<f.length; i++) {
            if (f[i] > max_fitness) {
                max_fitness = f[i];
                best_position = i;
            }
        }
        return best_position; // returning the position of highest fitness
    }
    

    public static int min_fitness(double[] f) {
        double min_fitness = f[0];
        int worst_position = 0;   
        for (int i=1; i<f.length; i++) {
            if (f[i] < min_fitness) {
                min_fitness = f[i];
                worst_position = i;
            }
        }
        return worst_position; // returning the position of lowest fitness
    }

    public static void Load_Data(int ROW_SIZE, int COLUMN_SIZE) throws IOException {
        String train_file="train_data.txt"; //read training data
        try (Scanner tmp = new Scanner(new File(train_file))) {
          for (int i=0; i<ROW_SIZE; i++)
            for (int j=0; j<COLUMN_SIZE; j++)
              if(tmp.hasNextDouble()) 
              train[i][j]=tmp.nextDouble();
        tmp.close();
        }
    }

    public static void generate_population(int POPULATION_SIZE, int FEATURE_SIZE){
        //create initial population
        for(int j=0; j<POPULATION_SIZE; j++){ 
            for(int k=0; k<FEATURE_SIZE; k++){
                pop[j][k]= (Math.random());
            }
        }
    }

    public static void generate_fitness(double pop[][], double train[][]){
        // Run every individual through train data
        for(int h=0; h<POPULATION_SIZE; h++){
            double budget = 3000; //starting budget
            int stocks = 0; //no. of stocks bought

            // weight of every signal is set to 0
            for(int j=0; j<ROW_SIZE; j++){ 
                double current_price = train[j][0];
                for(int m=0; m<OPTIONS; m++){ 
                    decisions[m]= 0.0;
                }

                // Adding weights to the decision array: [buy, sell, hold]
                for(int i=1; i<COLUMN_SIZE; i++){
                    if(train[j][i] == 0.0){
                        decisions[2] += pop[h][i-1];
                    }else if(train[j][i] == 2.0){
                        decisions[1] += pop[h][i-1];
                    }else{
                        decisions[0] += pop[h][i-1];
                    }
                }

                int final_decision = max_fitness(decisions);
                if(final_decision == 0){ //buy
                    if(budget >= current_price){
                        while(budget >= current_price){
                            budget = budget - current_price;
                            stocks++;
                        }
                    }
                }else if(final_decision == 1){//sell
                    if(stocks > 0){
                        while(stocks > 0){
                            budget = budget + current_price;
                            stocks--;
                        }
                    }
                }else{
                    /* used for debugging of hold actions */
                    // System.out.println(final_decision + "///" + decisions[2]);
                }

                if(j == ROW_SIZE - 1){
                    double liquidation = stocks * current_price;
                    budget = budget + liquidation;
                }   
            }
            budget_fitness[h] = budget;
            System.out.print(" " + Math.round(budget_fitness[h] * 100.0) / 100.0); //rounds to 2 decimal places
        }
    }

    public static void GA(){

        int elitism = 5; //number of fittest individuals that replace the least fittest
        int tournament_k = 4; //number of individuals that participate in the tournament
        int tournament_times = 20; //times tournament takes place

        Random random = new Random();

        // Elitism code
        // Keeping the top 1 solutions
        double[] temp_fitness_max = new double[budget_fitness.length];
        double[] temp_fitness_min = new double[budget_fitness.length];
        int[] elite = new int[elitism];
        for(int i = 0; i< budget_fitness.length; i++){
            temp_fitness_max[i] = budget_fitness[i];
            temp_fitness_min[i] = budget_fitness[i];
        }
        for(int i = 0; i < elitism; i++){
            int top_solution = max_fitness(temp_fitness_max);
            int bot_solution = min_fitness(temp_fitness_min);
            for(int x = 0; x< FEATURE_SIZE; x++){
                pop[bot_solution][x] = pop[top_solution][x];
            }
            elite[i] = top_solution;
            temp_fitness_max[top_solution] = 0.0;
            temp_fitness_min[bot_solution] = 1000000.0;
        }

        //tournament selection  
        int[] random_array = new int[tournament_times];
        double[] random_fitness_array = new double[tournament_times];
        int[] winners = new int[tournament_times];

        for(int z=0; z < tournament_times; z++){

            // Select random individuals
            for(int i=0; i < tournament_k; i++){
                int random_individual = random.nextInt(POPULATION_SIZE);
                if(random_individual != elite[0]){
                    random_array[i] = random_individual;
                    random_fitness_array[i] = budget_fitness[random_array[i]];
                }else{
                    i--;
                }
            }

            // Select that with max fitness
            winners[z] = random_array[max_fitness(random_fitness_array)];
        }



        //code for mutation  
        double mutation_probability = 0.2;
        int mut_counter = 0;

        for(int i=0; i<POPULATION_SIZE; i++){
            double random_probability = Math.random();
            if(mutation_probability > random_probability){
                int random_individual = random.nextInt(POPULATION_SIZE);
                if(random_individual != elite[0]){
                    mut_counter++;
                    int random_feature = random.nextInt(FEATURE_SIZE);
                    pop[random_individual][random_feature] = Math.random();
                }
            }
        }


        // code for crossover
        double crossover_probability = 0.8;
        int cr_counter = 0;
        for(int i = 0; i < tournament_times/4; i++){
            int random_individual = random.nextInt(winners.length);
            int other_random_indiviual = random.nextInt(winners.length);
            double random_probability = Math.random();
            int crossover_point1 = random.nextInt(FEATURE_SIZE); 
            int crossover_point2 = random.nextInt(FEATURE_SIZE); 
            if(crossover_point1 < crossover_point2 && random_probability < crossover_probability && random_individual != elite[0] && other_random_indiviual != elite[0]){
                double[] temp_array = new double[crossover_point2 - crossover_point1 + 1];
                cr_counter++;
                for(int x = crossover_point1; x < crossover_point2; x++){
                    temp_array[x - crossover_point1] = pop[winners[random_individual]][x];
                    pop[winners[random_individual]][x] = pop[winners[other_random_indiviual]][x];
                    pop[winners[other_random_indiviual]][x] = temp_array[x - crossover_point1];
                }
            }else if(crossover_point1 > crossover_point2 && random_probability < crossover_probability && random_individual != elite[0] && other_random_indiviual != elite[0]){
                double[] temp_array = new double[crossover_point1 - crossover_point2 + 1];
                cr_counter++;
                for(int x = crossover_point2; x < crossover_point1; x++){
                    temp_array[x - crossover_point2] = pop[winners[random_individual]][x];
                    pop[winners[random_individual]][x] = pop[winners[other_random_indiviual]][x];
                    pop[winners[other_random_indiviual]][x] = temp_array[x - crossover_point2];
                }
            }else if(random_probability < crossover_probability && random_individual != elite[0] && other_random_indiviual != elite[0]){
                cr_counter++;
                double temp = pop[winners[random_individual]][crossover_point1];
                pop[winners[random_individual]][crossover_point1] = pop[winners[other_random_indiviual]][crossover_point1];
                pop[winners[other_random_indiviual]][crossover_point1] = temp;
            }
        }

        System.out.println("Mutations: " + mut_counter);
        System.out.println("Crossovers: " + cr_counter);


    }
}
