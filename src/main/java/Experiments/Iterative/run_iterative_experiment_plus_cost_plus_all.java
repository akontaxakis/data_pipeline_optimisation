package Experiments.Iterative;

import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;
import util.lib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class run_iterative_experiment_plus_cost_plus_all {


    public static void main(String[] args) {
        lib utils = new lib();
        String uid = "ex100";
        uid = "b8i150";

        uid = "LOtoSingle2";

        uid = "singleLP_F";
        uid = "ALLLP_F";
        //uid = "b100s1000000";
        double edge_cost_modifier = 1000;

        int iterations =96;
        int b = 10;
        Set<String> lt_uniqueRequests = new HashSet<>();
        Set<String> eq_uniqueRequests = new HashSet<>();

        String project_path= "C:\\Users\\adoko\\PycharmProjects\\pythonProject1\\";
        for(int budget=0; budget<b;budget++){
            double shared_cummulative_cost =0;
            double limited_cummulative_cost = 0;
            double equivalent_cummulative_cost = 0;
            for (int iteration = 0; iteration < iterations; iteration++) {
                ////////////////////////////READ THE GRAPHS////////////////////////////////////
                HyperGraph shared = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid,iteration , "shared");
                HyperGraph limited = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid+"_"+budget, iteration, "limited");
                HyperGraph equivalent = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid+"_"+budget, iteration, "equivalent");
                ////////////////////////////REMOVE SUPER NODES/////////////////////////////////
                HyperGraph true_limited = utils.TurnToTrueHG(limited);
                HyperGraph true_shared = utils.TurnToTrueHG(shared);
                HyperGraph true_equivalent = utils.TurnToTrueHG(equivalent);

                ////////////////////////////READ REQUESTS///////////////////////////////////
                ArrayList<String> ltSet = utils.read_requests(project_path, iteration, uid+"_"+budget);
                ArrayList<String> eqSet_ = utils.read_requests(project_path, iteration, uid+"_eq_"+budget);

                String operation = ltSet.remove(ltSet.size() - 1);
                double lt_extra_cost = Double.parseDouble(ltSet.remove(ltSet.size() - 1));

                if (ltSet.size() == 0) {
                    ltSet.add(operation);
                }

                eqSet_.remove(eqSet_.size()-1);
                double eq_extra_cost = Double.parseDouble(eqSet_.remove(eqSet_.size()-1));
                if (eqSet_.size() == 0) {
                    eqSet_.add(operation);
                }


                ArrayList<String> eqSet = utils.euqivalentRequests(eqSet_);
                //String operation = "X_SiStPCLi_predict";

                ArrayList<Artifact> lt_rq = true_limited.getRequests(new HashSet<>(ltSet));
                ArrayList<Artifact> sh_rq = true_shared.getRequests(operation);
                ArrayList<Artifact> eq_rq = true_equivalent.getRequests(new HashSet<>(eqSet));

                Proposition_plan sh_plan = true_shared.expand_closest_proposition(sh_rq);
                Proposition_plan lt_plan = true_limited.expand_closest_proposition(lt_rq);
                Proposition_plan eq_plan = true_equivalent.expand_closest_proposition(eq_rq);

                double sh_cost = sh_plan.getCost();
                double lt_cost = lt_plan.getCost() + (lt_extra_cost * 10000);
                double eq_cost = eq_plan.getCost() + (eq_extra_cost * 10000);

                shared_cummulative_cost = shared_cummulative_cost + sh_cost;
                limited_cummulative_cost = limited_cummulative_cost + lt_cost;
                equivalent_cummulative_cost = equivalent_cummulative_cost + eq_cost;
                if(iteration < iterations-1)
                    lt_uniqueRequests.add(operation);

                if(sh_cost < lt_cost &  lt_cost < eq_cost){
                    //System.out.println("problem in " + iteration);
                }
                //System.out.println("t_shared" + t_shared_p.getCost());
                //System.out.println("t_limited_p -> " + limited_cost);
                //System.out.println(iteration+","+t_shared_p.getCost()+","+limited_cost+","+eq_cost);
                //System.out.println(iteration + "," + equivalent_cummulative_cost + "," + limited_cummulative_cost + "," + shared_cummulative_cost);


               if(iteration == iterations-1 & budget == 0) {
                    ArrayList<String> eqSet_f = utils.euqivalentRequests(new ArrayList<>(lt_uniqueRequests));
                    //String operation = "X_SiStPCLi_predict";
                    ArrayList<Artifact> lt_rq_f = true_limited.getRequests(lt_uniqueRequests);
                    lt_uniqueRequests.add(operation);
                    ArrayList<Artifact> sh_rq_f = true_shared.getRequests(lt_uniqueRequests);
                    ArrayList<Artifact> eq_rq_f = true_equivalent.getRequests(new HashSet<>(eqSet_f));

                    Proposition_plan sh_plan_f = true_shared.expand_closest_proposition(sh_rq_f);
                    Proposition_plan lt_plan_f = true_limited.expand_closest_proposition(lt_rq_f);
                    Proposition_plan eq_plan_f = true_equivalent.expand_closest_proposition(eq_rq_f);

                    double sh_cost_f = sh_plan_f.getCost();
                    double lt_cost_f = lt_plan_f.getCost() + (lt_extra_cost * 10000);
                    double eq_cost_f = eq_plan_f.getCost() + (eq_extra_cost * 10000);
                    System.out.println(budget + "," + sh_cost_f/edge_cost_modifier + "," + lt_cost_f/edge_cost_modifier + "," + eq_cost_f/edge_cost_modifier);

                }


            }
            //System.out.println(budget + "," + shared_cummulative_cost + "," + limited_cummulative_cost + "," + equivalent_cummulative_cost);
            double dataset_size = 0.8;
            double budget_cost = dataset_size * 0.023 ;
            double computation_cost_per_hour = 0.63;
            double computation_cost_per_second = computation_cost_per_hour/3600;
            double shared_cost = shared_cummulative_cost/edge_cost_modifier*computation_cost_per_second + budget_cost;
            if(budget > 0)
            budget_cost = (0.0000008 * Math.pow(10,budget) * 0.023) + 0.8 * 0.023;
            double limited_cost = limited_cummulative_cost/edge_cost_modifier*computation_cost_per_second + budget_cost;
            double equivalent_cost = equivalent_cummulative_cost/edge_cost_modifier*computation_cost_per_second+ budget_cost;
            System.out.println(budget + "," + shared_cummulative_cost/edge_cost_modifier + "," + limited_cummulative_cost/edge_cost_modifier + "," + equivalent_cummulative_cost/edge_cost_modifier + "," + shared_cost + "," + limited_cost + "," + equivalent_cost);

        }
    }

}
