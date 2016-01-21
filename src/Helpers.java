/**
 * Created by natalia on 2014-02-26.
 */


/*
 Rules:
 the affordance of the object depends on the sub-activity
it is involved in. For example, a cup has the affordance
of ‘pour-to’ in a pouring sub-action and has
the affordance of ‘drinkable’ in a drinking sub-action.



 */
public class Helpers {

    // From http://www.dma.fi.upm.es/java/fuzzy/fuzzyinf/funpert_en.htm
    // and http://www.bindichen.co.uk/post/AI/fuzzy-inference-membership-function.html
    // A Gaussian MF is determined complete by c and σ; c represents the MFs centre and σ determines the MFs width.
//    static float getMembershipDegreeToGaussianFunction(int x, float mean, float stdev){
//        //The full width at tenth of maximum (FWTM) for a Gaussian could be of interest and is.   (Wikipedia
//        float width = 4.29193f* stdev;
//        mean = (float) (currentFrame-(width/2));
//        BigDecimal bd = new BigDecimal(Math.exp(-1*(Math.pow(x-mean, 2)) / (2* Math.pow(stdev,2))));
//        return bd.setScale(2,BigDecimal.ROUND_HALF_EVEN).floatValue();
//    }
//
//    static float getMembershipDegreeToLeftShoulderFunction(int x, float x1, float x2, float a, float b){
//        //The full width at tenth of maximum (FWTM) for a Gaussian could be of interest and is.   (Wikipedia
//        //if (x<return bd.setScale(2,BigDecimal.ROUND_HALF_EVEN).floatValue();
//        return 1.0f;
//    }

//    protected static String[] activityWithHighestCertainty(ArrayList<Double> degrees){
//        String activity = "null";
//        Double max = 0.0;
//        String [] highestCertaintyActiv = new String[2];
//        for (int i=0; i< degrees.size(); ++i){
//            if (degrees.get(i)> max) {
//                max = degrees.get(i);
//                activity = indexes.get(i);
//            }
//        }
//        highestCertaintyActiv[0] = activity;
//        highestCertaintyActiv[1] = Double.toString(max);
//        return highestCertaintyActiv;
//    }
//
//    protected static void LearnSubActivitiesInvolvedInActivities(String file){
//        try{
//            FileInputStream fstream = new FileInputStream(file);       //"dataset.txt"
//            // Get the object of DataInputStream
//            DataInputStream in = new DataInputStream(fstream);
//            BufferedReader br = new BufferedReader(new InputStreamReader(in));
//            String strLine;
//            listOfActivitySets = new ArrayList<Set>(SubActivities.length);
//            for(int i = 0; i<SubActivities.length; i++){
//                listOfActivitySets.add(new HashSet<String>());
//            }
//            //Read File Line By Line
//            while ((strLine = br.readLine()) != null){ //for each sub-activity register
//                // Print the content on the console
//                String[] register = strLine.split(",");
//                for (int i=0; i<register.length; ++i)
//                    register[i] = removeQuotes(register[i]);
//                // We add the action to its activity set
//                int index = activities.get(register[1]);
//                listOfActivitySets.get(index).add(register[3]);
//            }
//
//            for(int i = 0; i<listOfActivitySets.size(); i++){
//                System.out.println(listOfActivitySets.get(i));
//            }
//            //Close the input stream
//            in.close();
//        }
//        catch (Exception e){//Catch exception if any
//            System.err.println("LearnActionsInvolvedInActivities: Error reading dataset file: " + e.getMessage());
//        }
//    }
}

/*Redondeo para suma = 1 o menor:
        float weight = (float) subActivitiesRates[i][j]/activitiesSums.get(i);
        System.out.println("Before " + weight);
        BigDecimal bd = new BigDecimal(weight);
        //String.format("%.2f", weight);
        DecimalFormat df = new DecimalFormat("0.##");
        //weight = (float) Float.valueOf(df.format(weight));
        //weight = new Float(Math.round(weight*100)/100.00);
        //weight = new Float(Math.floor(weight * 100 +.5)/100);
        //weight = round(weight,2);
        String r = String.format("%.2f", weight);
        weight = round(weight, 2);
        System.out.println("after"+weight);
        //weight = roundTwoDecimals(weight);
        bd=bd.setScale(2,BigDecimal.ROUND_HALF_EVEN);//ROUND_HALF_UP);//ROUND_FLOOR);//.ROUND_DOWN);//ROUND_HALF_DOWN);//ROUND_HALF_EVEN);
        ruleWeights[i][j] = weight ;//bd.floatValue();
        partialSum += weight;*/



//TESTS

// UPDATE TEST
// Learning: updating a certainty value over a same axiom (with same instance) does not work, only first value input is preserved through time!!
//            insertSubActivity("drinking1", "drinking", 0.6, 23, 36);
//            kb.solveKB();
//            float c = getCertaintyOfActivityHappening("medicine");
//            insertSubActivity("drinking1", "drinking", 0.5, 37, 86);
//            kb.solveKB();
//            float d = getCertaintyOfActivityHappening("medicine");
//            insertSubActivity("drinking1", "drinking", 0.6, 37, 86);
//            kb.solveKB();
//            float e = getCertaintyOfActivityHappening("medicine");
//            insertSubActivity("drinking1", "drinking", 0.8, 37, 86);
//            kb.solveKB();
//            float f = getCertaintyOfActivityHappening("medicine");
//            System.out.println("Updating certainties before->"+c+" after-> "+ d+"after->"+e+" after-> "+ f );
//            ///
//            insertSubActivity("drinking1", "drinking", 0.0, 37, 86);
//            kb.solveKB();
//            float g = getCertaintyOfActivityHappening("medicine");
//            insertSubActivity("drinking1", "drinking", 0.3, 37, 86);
//            kb.solveKB();
//            float h = getCertaintyOfActivityHappening("medicine");
//            insertSubActivity("drinking1", "drinking", 0.2, 23, 36);
//            kb.solveKB();
//            float i = getCertaintyOfActivityHappening("medicine");
//            insertSubActivity("drinking1", "drinking", 0.9, 37, 86);
//            kb.solveKB();
//            float j = getCertaintyOfActivityHappening("medicine");
//            System.out.println("Updating certainties before->"+g+" after-> "+ h+"after->"+i+" after-> "+ j);
//Updating certainties before->0.09 after-> 0.09after->0.108 after-> 0.144 for different instances
/*

    static protected  boolean FTWFulfillsOrderConstraintPatterns(ArrayList<String> subActivitySequence, ArrayList<Dictionary> activitiesOccurredInLastFTW){
        boolean subActivityFound;
        boolean continueSearching = true;
        int i= 0;
        for (int k=0; k<subActivitySequence.size() && continueSearching; ++k){ // SubActivities in a pattern
            subActivityFound = false;
            if (activitiesOccurredInLastFTW.get())
                while (i<activitiesOccurredInLastFTW.size() && !subActivityFound){
                    if(activitiesOccurredInLastFTW.get(i).get("subActivity").equals(subActivitySequence.get(k))){
                        subActivityFound = true;
                        //activitiesFulfillingConstraint.add(activitiesOccurredInLastFTW.get(k).get(""));
                        i++;


                    }
                    if (!subActivityFound)
                        continueSearching = false;
                }*/
/*// 1. Retrieve rules (or general variable in this case since we need the value of a concrete individual's data property -concrete role-)
            Concept Rules = new Concept("Rules");
            Concept placing = kb.getConcept("placing");
            // 2. Retrieve individual to ask for its data property.
            Individual placingI = kb.getIndividual("placing8");//new Individual("placing8");
            kb.addDatatypeRestriction(placing.EXACT_VALUE, 5, "hasStartFrame"); // How to assign it to an individual instead of a concept?

            // 3. Query for the defuzzified value of its data property
            //LomDefuzzifyQuery q1 = new LomDefuzzifyQuery(Rules, placingI, "hasStartFrame");  // Result is 20.
            //LomDefuzzifyQuery q1 = new LomDefuzzifyQuery(Rules,placingI,"hasStartFrame");
            MomDefuzzifyQuery q1 = new MomDefuzzifyQuery(Rules,placingI,"hasStartFrame");
            //LomDefuzzifyQuery(Concept c, Individual i, String fName);
//
            //MomDefuzzifyQuery q2= new MomDefuzzifyQuery(subActivityConcept, subActivityIndividual, "hasStartFrame");
            //SomDefuzzifyQuery q3= new SomDefuzzifyQuery(subActivityConcept, subActivityIndividual, "hasStartFrame");
            //kb.milp.showVars.addConcreteFillerToShow("hasStartFrame");

            Solution s = q1.solve(kb);
            System.out.println("MomDefuzzifyQuery: "+s.getSolution());
            // Show the value of a variable
            //kb.milp.showVars.addVariable(Variable var, String varName);
            // Show the value of all fillers of a concrete role
            kb.milp.showVars.addConcreteFillerToShow("hasStartFrame");
            //kb.milp.showVars.addConcreteFillerToShow("hasStartFrame", subActivityName);
            //kb.milp.showVars.showConcreteFillers("hasStartFrame", subActivityName);*/     /// CRASH





// UPDATE TEST
            /*insertSubActivity("drinking1", "drinking", 0.6, 23, 36, "");
            kb.solveKB();
            float c = getMinFDLCertaintyOfActivityHappening("medicine");
            insertSubActivity("drinking1", "drinking", 0.5, 37, 86, "");
            kb.solveKB();
            float d = getMinFDLCertaintyOfActivityHappening("medicine");
            insertSubActivity("drinking1", "drinking", 0.6, 37, 86, "");
            kb.solveKB();
            float e = getMinFDLCertaintyOfActivityHappening("medicine");
            insertSubActivity("drinking1", "drinking", 0.8, 37, 86, "");
            kb.solveKB();
            float f = getMinFDLCertaintyOfActivityHappening("medicine");
            System.out.println("Updating certainties with MinInstance before->"+c+" after-> "+ d+"after->"+e+" after-> "+ f );

            insertSubActivity("drinking1", "drinking", 0.6, 23, 36, "");
            kb.solveKB();
            float g = getMaxFDLCertaintyOfActivityHappening("medicine");
            insertSubActivity("drinking1", "drinking", 0.5, 37, 86, "");
            kb.solveKB();
            float h = getMaxFDLCertaintyOfActivityHappening("medicine");
            insertSubActivity("drinking1", "drinking", 0.7, 37, 86, "");
            kb.solveKB();
            float i = getMaxFDLCertaintyOfActivityHappening("medicine");
            insertSubActivity("drinking1", "drinking", 0.8, 37, 86, "");
            kb.solveKB();
            float j = getMaxFDLCertaintyOfActivityHappening("medicine");
            System.out.println("Updating certainties with MaxInstance before->"+g+" after-> "+ h+"after->"+i+" after-> "+ j );
            //IMPRIME:    Updating certainties before->0.108 after-> 0.108after->0.108 after-> 0.108*/



/*public static String getActivityWithHighestCertainty(ArrayList<Dictionary> activitiesDetected){
        float max = 0.0f;
        float certainty;
        String activity = "";
        for (int i=0; i<activitiesDetected.size(); ++i){
            System.out.println("Activity: "+activitiesDetected.get(i).get("activityConcept") +" Cert: "+activitiesDetected.get(i).get("certainty"));
            certainty = Float.parseFloat(activitiesDetected.get(i).get("certainty").toString());
            if ( certainty > max){
                max = certainty;
                activity = activitiesDetected.get(i).get("activityConcept").toString();//Float.parseFloat(activitiesDetected.get(i).get("activityConcept"));
            }
        }
        return activity;
    }*/





//Successful tests:

///// TEST INSERTING AXIOMS AND QUERY
            /*insertConceptAssertionFDL("reaching1", "reaching", defaultDegree);
            insertConceptAssertionFDL("moving1", "moving", defaultDegree);
            insertConceptAssertionFDL("pouring1", "pouring", defaultDegree);
            //insertConceptAssertionFDL("moving2", "moving", defaultDegree);
            insertConceptAssertionFDL("placing1", "placing", defaultDegree);

            insertObjectPropertyAssertionFDL(individualName, "reaching1", "performsSubActivity", new Double(1.0));
            insertObjectPropertyAssertionFDL(individualName, "moving1", "performsSubActivity", new Double(1.0));
            insertObjectPropertyAssertionFDL(individualName, "pouring1", "performsSubActivity", new Double(1.0));
            //insertObjectPropertyAssertionFDL(individualName, subActivityIndividualName, "performsSubActivity", (Double)window.get(i).get("certainty"));
            insertObjectPropertyAssertionFDL(individualName, "placing1", "performsSubActivity", new Double(1.0));

            insertConceptAssertionFDL("bowl1", "bowl", defaultDegree);
            insertConceptAssertionFDL("box1", "box", defaultDegree);
            insertConceptAssertionFDL("milk1", "milk", defaultDegree);

            insertObjectPropertyAssertionFDL("reaching1", "bowl1", "usesObject", new Double(1.0));
            insertObjectPropertyAssertionFDL("reaching1", "box1", "usesObject", new Double(1.0));
            insertObjectPropertyAssertionFDL("reaching1", "milk1", "usesObject", new Double(1.0));
            insertObjectPropertyAssertionFDL("moving1", "bowl1", "usesObject", new Double(1.0));
            insertObjectPropertyAssertionFDL("moving1", "box1", "usesObject", new Double(1.0));
            insertObjectPropertyAssertionFDL("moving1", "milk1", "usesObject", new Double(1.0));
            insertObjectPropertyAssertionFDL("pouring1", "bowl1", "usesObject", new Double(1.0));
            insertObjectPropertyAssertionFDL("pouring1", "box1", "usesObject", new Double(1.0));
            insertObjectPropertyAssertionFDL("pouring1", "milk1", "usesObject", new Double(1.0));
            insertObjectPropertyAssertionFDL("placing1", "bowl1", "usesObject", new Double(1.0));
            insertObjectPropertyAssertionFDL("placing1", "box1", "usesObject", new Double(1.0));
            insertObjectPropertyAssertionFDL("placing1", "milk1", "usesObject", new Double(1.0));
            kb.solveKB();
            System.out.println("Certainty of Cereal: "+getCertaintyOfActivityHappeningFDL("cereal"));*/




/*static protected ArrayList<String> filterActivitiesFulfillingAllConstraints(){
        ArrayList<String> activitiesSatisfyingAllConstraints = new ArrayList<String>();
        try{
            ArrayList<Dictionary> activitiesOccurredInLastFTW = new ArrayList<Dictionary>();
            boolean bingo = false;
            // 1. SubActivity Order constraints
            for (int i=0; i< orderConstraints.size(); ++i){ // Activity
                boolean continueCheckingActivity = true;
                activitiesOccurredInLastFTW = cutFTWFromLatestSubActivitiesForActivity(indexes.get(i)); // TODO: MORE EFFICIENT COMPARING BACKWARDS
                bingo = false;
                for(int j=0; j<orderConstraints.get(i).size() && continueCheckingActivity; j++){ // List of constraint patterns per activity
                    if(FTWFulfillsOrderConstraintPatterns(orderConstraints.get(i).get(j), activitiesOccurredInLastFTW))// TODO: Check repetitions of given patterns to give more weight on credibility of that activity being happening
                        bingo = true;
                    else{
                        continueCheckingActivity = false;
                        bingo = false;}
                }
                if (bingo && continueCheckingActivity){  // 2. OBJECT CONSTRAINTS (once fulfilled the order constraints)
                    System.out.print("******** Activity "+indexes.get(i)+". Order constraints passed by: ");
                    /*for (int i=0; i<detectedActivities.size(); i++){
                        activCertainties.add((float)getCertaintyOfActivityHappeningFDL(indexes.get(i))); // BIGGEST RUNNING TIME OVERHEAD: deleting KB, inserting axioms and querying.
                        candidateActivities.add(detectedActivities.get(i));
                        System.out.print(detectedActivities.get(i)+ " ");
                    }
                    if (detectedActivities.size()< 1){
                        classification[activities.get(activ)][activities.size()]+= 1; // Filling the null (last) column
                        System.out.println( "   -"+nActivities+ " Activity " + activ + " "+activityID+" detected as NULL ____________________________________ ");
                    }
                    else{ // FILTRAR LA DE MAYOR UMBRAL- ASIGNAR UMBRAL A PARTIR DEL NUMERO De PATRONES CUMPLIDOS/INTERACCIONES CON OBJETOS Y RESTRICCIONES DE OBJECT POS.
                        String detectedActivity = getActivityWithHighestCertaintyOverThreshold(activCertainties, candidateActivities, activDetectionThreshold); // Gives "" for cereal!
                        if(detectedActivity.equals(activ))
                            System.out.println("  ------------------------------------------ BINGO: " + activ );
                        else
                            System.out.println("   -"+nActivities+ " Activity " + activ + " "+activityID+" detected as: "+ detectedActivity);
                        if(!detectedActivity.equals("")) // if activity involves SubActivities of stackable, check their objects positions
                            classification[activities.get(activ)][activities.get(detectedActivity)]+= 1;
                        else classification[activities.get(activ)][activities.size()]+= 1;
                    }
                    //System.out.println("Fulfilling ORDER pattern");
                    activitiesSatisfyingAllConstraints.add(indexes.get(i));
                    //ArrayList<String> activitiesSatisfyingObjectConstraints = FTWFulfillsObjectConstraintPatterns((ArrayList<String>)objectsRequirements.get(indexes.get(i)), activitiesOccurredInLastFTW);
                    //Add y borrar
                    //if(activitiesSatisfyingObjectConstraints.size()>0) {// 3. OBJECT POSITION CONSTRAINTS
                        // TODO: remove duplicates //TODO: Detect more than one if over what threshold? (IN REAL TIME)// TODO: weight more those with +1 pattern recognized
                        //System.out.println("Fulfilling OBJECT patterns");
                        //activitiesSatisfyingAllConstraints.addAll(FTWFulfillsExtraConstraints(activitiesOccurredInLastFTW, activitiesSatisfyingObjectConstraints));
                }
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in filterActivitiesFulfillingAllConstraints: " + e.getMessage());
        }
        //System.out.println("Size of activities fulfillingAllConstraints: "+ activitiesSatisfyingAllConstraints.size());
        return activitiesSatisfyingAllConstraints;
    }

    static protected ArrayList<String> filterActivitiesFulfillingOrderConstraints(){
        ArrayList<String> activitiesSatisfyingAllConstraints = new ArrayList<String>();
        try{
            ArrayList<Dictionary> activitiesOccurredInLastFTW = new ArrayList<Dictionary>();
            boolean bingo = false;
            // 1. SubActivity Order constraints
            for (int i=0; i< orderConstraints.size(); ++i){ // Activity
                boolean continueCheckingActivity = true;
                activitiesOccurredInLastFTW = cutFTWFromLatestSubActivitiesForActivity(indexes.get(i)); // TODO: MORE EFFICIENT COMPARING BACKWARDS
                bingo = false;
                for(int j=orderConstraints.get(i).size()-1; j>=0 && continueCheckingActivity; j--){ // List of constraint patterns per activity
                    if(FTWFulfillsOrderAndObjectConstraintPatterns(i, (ArrayList<String>)orderConstraints.get(i).get(j), activitiesOccurredInLastFTW))// TODO: Check repetitions of given patterns to give more weight on credibility of that activity being happening
                        bingo = true;
                    else{
                        continueCheckingActivity = false;
                        bingo = false;}
                }
                if (bingo && continueCheckingActivity){
                    //System.out.println("Fulfilling ORDER pattern");
                    activitiesSatisfyingAllConstraints.add(indexes.get(i));
                    //ArrayList<String> activitiesSatisfyingObjectConstraints = FTWFulfillsObjectConstraintPatterns((ArrayList<String>)objectsRequirements.get(indexes.get(i)), activitiesOccurredInLastFTW);
                    //Add y borrar
                    //if(activitiesSatisfyingObjectConstraints.size()>0) {// 3. OBJECT POSITION CONSTRAINTS
                    // TODO: remove duplicates //TODO: Detect more than one if over what threshold? (IN REAL TIME)// TODO: weight more those with +1 pattern recognized
                    //System.out.println("Fulfilling OBJECT patterns");
                    //activitiesSatisfyingAllConstraints.addAll(FTWFulfillsExtraConstraints(activitiesOccurredInLastFTW, activitiesSatisfyingObjectConstraints));
                }
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in filterActivitiesFulfillingAllConstraints: " + e.getMessage());
        }
        //System.out.println("Size of activities fulfillingAllConstraints: "+ activitiesSatisfyingAllConstraints.size());
        return activitiesSatisfyingAllConstraints;
    }*/