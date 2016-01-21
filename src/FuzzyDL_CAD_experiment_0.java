import InputFiles.ActivitiesCertaintiesPairs;
import InputFiles.SubActivitiesSubSequence;
import fuzzydl.*;
import fuzzydl.exception.FuzzyOntologyException;
import fuzzydl.exception.InconsistentOntologyException;
import fuzzydl.milp.Solution;
import fuzzydl.parser.ParseException;
import fuzzydl.parser.Parser;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
//import jsc.onesample.Ttest;
//import org.apache.commons.io.FileUtils;
//import CriticalActivity;

//package net.sourceforge.jFuzzyLogic.test;

//import java.time.Period;

/**
 * Created with IntelliJ IDEA.
 * User: ndiaz
 * Date: 2014-01-27
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class FuzzyDL_CAD_experiment_0 {
    //Objetos 12 object affordance labels: reachable, movable, pourable, pourto, containable, drinkable, openable, placeable, closable, scrubbable, scrubber, stationary
    // 10 OBJECTS
    public static final String BOOK = "book"; // TODO: put activities in constants as well
    public static final String BOWL = "bowl";
    public static final String BOX = "box"; // Is Cereal considered a box?
    public static final String CLOTH = "cloth";
    public static final String CUP = "cup";
    public static final String MEDICINE_BOX = "medcinebox";
    public static final String MICROWAVE = "microwave";
    public static final String MILK = "milk";
    public static final String PLATE = "plate";
    public static final String REMOTE = "remote";
    public static final String NULL_OBJECT = "nullObject";
    public static final ArrayList<String> objects = new ArrayList<String>(Arrays.asList(BOOK, BOWL, BOX, CLOTH, CUP, MEDICINE_BOX, MICROWAVE, MILK, PLATE, REMOTE));
    // HIGHER CATEGORIES IN THE CLASSIFICATION
    public static final String OBJECT = "object"; // INCLUDES ALL
    public static final String KITCHENWARE = "kitchenware"; //(object, bowl, cloth, cup, plate, microwave)
    public static final String STACKABLE = "stackable"; // (all except microwave)
    public static final String EDIBLE = "edible"; // milk, pitcher of cereals?, pizza box, medicinebox(assuming pills are detected through its box)
    public static final String MOVABLE = "movable"; // microwave = (not movable and not pickable), the rest is movable.
    public static final String PICKABLE = "pickable"; // all but microwave. In dataset: box and remote
    public static final String FILLABLE = "fillable"; //Can be filled: bowl, cup, plate
    public static final String CLEANABLE = "cleanable"; // kitchenWare, microwave, box(if other than pizza's?), book
    public static final String OPENABLE = "openable"; // microwave, book, medicinebox?(**check if it goes with opening), +fridge, cupboard
    public static final String CLEANER = "cleaner"; // cloth
    public static final String EXCLUSIVE_OBJECT = "exclusiveObject";// Objects that are only used within a unique activity
    public static final String CONTAINER_KITCHENWARE = "containerKitchenware"; // bowl, cup
    public static final String ARRANGEABLE = "arrangeable";// (BOOK, BOX)
    public static final String DRINKING_KITCHENWARE = "drinkingKitchenware";
    public static final ArrayList<String> objectCategoryClasses = new ArrayList<String>(Arrays.asList(OBJECT, KITCHENWARE, STACKABLE, EDIBLE, MOVABLE, PICKABLE, FILLABLE, CLEANABLE, OPENABLE, CLEANER, EXCLUSIVE_OBJECT, ARRANGEABLE));
    // 10 sub-actividades (including null)
    public static final String REACHING = "reaching"; // TODO: put Activities in constants as well
    public static final String MOVING = "moving";
    public static final String PLACING = "placing";
    public static final String OPENING = "opening";
    public static final String CLOSING = "closing";
    public static final String EATING = "eating";
    public static final String DRINKING = "drinking";
    public static final String POURING = "pouring";
    public static final String CLEANING = "cleaning";
    public static final String NULL_SA = "null";
    // 10 Activities
    public static final String CEREAL = "cereal";
    public static final String MEDICINE = "medicine";
    public static final String STACKING = "stacking";  // Object positions are needed to distinguish among these two activities (stacking and unstacking)
    public static final String UNSTACKING = "unstacking";
    public static final String MICROWAVING = "microwaving"; // Microwaving food
    public static final String BENDING = "bending"; // = Picking objects
    public static final String CLEANING_OBJECTS ="cleaningObjects"; // cleaningObjects in the fileKB**  (referred as scrubbing in the paper)
    public static final String TAKEOUT = "takeout"; // =Taking food
    public static final String ARRANGING_OBJECTS = "arrangingObjects"; // = Arranging objects in the paper (placing in the training file)
    public static final String EATING_MEAL = "eatingMeal";


    // RULES: if object = cup && doesNotContain(subActivity, microwave) => Activity= medicine
    // if object = edible && subActivity=  => Activity = IngestActivity
    // if object = kitchenware => Activity = ActivityInTheKitchen
    protected static ArrayList<Dictionary> KBmirrorSubActivities  = new ArrayList<Dictionary>();
    protected static ArrayList<String> KBmirrorActivID  = new ArrayList<String>();
    protected static ArrayList<String> KBmirrorActivConcept  = new ArrayList<String>();
    protected static ArrayList<Integer> KBmirrorActivStartFrames  = new ArrayList<Integer>();
    protected static ArrayList<Integer> KBmirrorActivEndFrames  = new ArrayList<Integer>();
    protected static HashMap<String, ArrayList<String>> objectCategories = new HashMap<String, ArrayList<String>>();
    //protected static ArrayList<ArrayList<HashMap<String, ArrayList<String>>>>
    protected static ArrayList<ArrayList<String>>  objectCategoriesConstraints = new ArrayList<ArrayList<String>>();//HashMap<String, ArrayList<String>>>>();
    protected static ArrayList<String> distinguishableAmongChallengingActivities = new ArrayList<String>(Arrays.asList(CLEANING_OBJECTS));
    protected static ArrayList<String> groupsOfChallengingSimilarActivities = new ArrayList<String>();
    protected static HashMap<String, ArrayList<String>> reachMilkOrBowlOrBox, moveMilkOrBowlOrBox, placeMilkOrBowlOrBox, openMilkOrBox, pourMilkOrBox, reachCupOrMedicineBox, moveCupOrMedicineBox, placeCupOrMedicineBox, openMedicineBox, eatMedicineBox, drinkCup, reachStackable, moveStackable, placeStackable, nullSA, reachMicroOrDrinkingKitchenwareOrBox, moveDrinkingKitchenwareOrBox, placeDrinkingKitchenwareOrBox, reachMicro, openMicro, closeMicro, reachPickable, movePickable, reachMicroOrCloth, moveCloth, placeCloth, cleanMicroOrCloth,
    reachContainerKitchenwareOrMicro, moveContainerKitchenware, placeContainerKitchenware, reachArrangeable, moveArrangeable, placeArrangeable, reachCup, moveCup, placeCup, eatCup;
    //protected static ArrayList<ArrayList<ArrayList<String>>> orderConstraints = new ArrayList<ArrayList<ArrayList<String>>>(); // Sub-activities order constraints per Activity
    protected static ArrayList<ArrayList<HashMap<String, ArrayList<String>>>> orderObjectsConstraints= new ArrayList<ArrayList<HashMap<String, ArrayList<String>>>>();// ArrayList<List<HashMap<String, ArrayList<String>>>>();//ArrayList<ArrayList<ArrayList<ArrayList<String>>>>(); // Sub-activities order constraints per Activity
    protected static ArrayList<ArrayList<SubActivitiesSubSequence>> orderObjectsSubSequenceConstraints= new ArrayList<ArrayList<SubActivitiesSubSequence>>();// ArrayList<List<HashMap<String, ArrayList<String>>>>();//ArrayList<ArrayList<ArrayList<ArrayList<String>>>>(); // Sub-activities order constraints per Activity
    protected static KnowledgeBase kb = new KnowledgeBase();
    protected static Parser parser;
    protected static Individual userIndividual;
    protected static HashMap<String, Integer> activities = new HashMap<String, Integer>();
    protected static HashMap<String, Integer> subsumesBySAAvgLengthCategory = new HashMap<String, Integer>();
    protected static HashMap<Integer, String> indexes = new HashMap <Integer, String>();
    protected static ArrayList<ArrayList<String>> subActivitiesInvolvedInActivity = new ArrayList<ArrayList<String>>();
    protected static ArrayList<ArrayList<String>> subsumesPairsBySubAct = new ArrayList<ArrayList<String>>();
    protected static ArrayList<Dictionary> detectedCriticalActivities = new ArrayList<Dictionary>();
    protected static ArrayList<Float> activCertainties = new ArrayList<Float>();
    protected static ArrayList<String> candidateActivities = new ArrayList<String>();
    protected static ArrayList<String> SubActivities = new ArrayList<String>(Arrays.asList("reaching", "moving", "placing", "opening",  "closing", "eating", "drinking","pouring", "cleaning", "null")); //cleaning is old scrubbing
    //protected static ArrayList<String> UserPerformsSubActivitiesAxioms = new ArrayList<String>(Arrays.asList("reach", "move", "place", "open",  "close", "eat", "drink","pour", "clean", "nullSA")); //cleaning is old scrubbing
    protected static ArrayList<ArrayList<String>> userPerformsSubActivitiesObjectAxioms = new ArrayList<ArrayList<String>>(activities.size()); //cleaning is old scrubbing
    protected static ArrayList<String> criticalSubActivities = new ArrayList<String>(Arrays.asList("reaching", "moving","placing", "opening","eating", "drinking", "medicine")); //TODO: includes Activities as well
    protected static ArrayList<ArrayList<Float>> activitiesDurationsMean_Stdev = new ArrayList<ArrayList<Float>>(); // Activities avg durations (mean, std dev and max duration)
    protected static ArrayList<Integer> activitiesDurationsInNrOfSubActivities = new ArrayList<Integer>(); // Activities avg durations (mean, std dev and max duration)
    protected static Query startQuery;
    protected static Solution startQuerySolution;
    protected static Dictionary lastSubActivityOccurred = new Hashtable();
    protected static ArrayList<String> usedObjects = new ArrayList<String>();
    protected static ArrayList<String> usedObjectsIDs = new ArrayList<String>();
    protected static ArrayList<Float> usedObjectsCertainties = new ArrayList<Float>();
    protected static ArrayList<ArrayList<Float>> objectsPositions = new ArrayList<ArrayList<Float>>();
    //Fuzzy
    protected static LeftConcreteConcept closeObjectInteraction;
    //protected static String maxDurationStackUnstacking = STACKING;
    //  ******************* SETTINGS *****************************
    protected static String individualName = "Natalia";  // Unique user for now // TODO: Multiuser
    protected static ArrayList<Integer> usersIDs = new ArrayList<Integer>(Arrays.asList(1,3,4,5));
    protected static int timeWindowInFrames = 300;  // Used to check for occurrence of critical Activities and SubActivities
    protected static int KBLifetimeInFrames = 0;
    protected static int currentFrame = 0;
    protected static int firstFrame = 0; //    protected static int frameRate = 30; // fps
    protected static int precedesThresholdInFrames = 5;
    protected static int realFrameClock = 0;
    protected static float activityDetectionThreshold = 0.24f;//0.4f; //0.7f;//0.6f;
    protected static float activityDetectionSecondaryThreshold = 0.17f;
    protected static float activityPreRatioDetectionThreshold = 0.75f;//used for giving second opportunities;
    protected static float fastPreselectionRatioThreshold = 0.5f;//0.5f;
    protected static float SAObjectSubSequencesRatioThreshold = 0.67f;//75
    protected static float minSubActDetectionThreshold = 0.5f;
    protected static float minDiffForConsideringDraw = 0.25f;//0.18f;//0.2f;//0.3f
    // 0.2: ---> Avg precision and recall: 0.79 0.87 for total: 769 SubActivities simulated, 92 Activities and 80886 objectEvents. KBmirrorSubAct: 40    ---> Accuracy: 0.76
    protected static ArrayList<Float> activitiesDetectionThresholds;
    protected static int closeObjectInteractionA = 480;//52; con 480 y 530 no funciona, coge demasiados objetos y estos resultan en demasiadas instancias en la kb? filtro sa-obj no funciona en ambos
    protected static int closeObjectInteractionB = 700;//530;//80;
    protected static int nSubActivitiesDetected = 0;
    protected static int timeWindowSizeRatio = 1; // For taking equivalent number of highest certainty detected sub-activities.
    protected static float [][] ruleWeights;
    //protected static int criticalTimeWindowInMinutes = 3; // TODO:initialize in minutes depending on frameRate in initialization method
    protected static Double defaultDegree = 1.0;  // TODO **STACKING BOXES VALUES? 160cm diff in z axis. 19 in x, and  66 in y.
    // TODO:add fuzzy property touchingObjectClose(from article, considered as error threshold and due to not completely accurate object interaction detection of HW)
    // x     y   z   -> = sameXThr, touchingAlongYAxisThr, sameZThreshold
    // 5-10, 70, 50-100
    protected static int nearAlongXAxisThreshold = 700;//180; //125  100
    protected static int touchingAlongYAxisThreshold = 100; // 75  50
    protected static int nearAlongZAxisThreshold = 700;//125;
    protected static int sameXThreshold = 30;//15;//75;//50;
    protected static int sameYThreshold = 75;//50; // TODO: lower?
    protected static int sameZThreshold = 100;//75
    protected static String workingPath = ".\\";//FuzzyDL_CAD_experiment_0.class.getProtectionDomain().getCodeSource().getLocation().getPath();//"C:/Users/natalia/Dropbox/a-Evaluation PhD/Simulator/FuzzyHAR/";
    protected static String trainingFile = workingPath.concat("activity.csv");
    protected static String trainingFileWithUsers = workingPath.concat("activityWithUsers.csv");
    //protected static String testFile = "C:/Users/natalia/Dropbox/a-Evaluation PhD/Simulator/SubActivitiesAndObjectsWithUsers.csv";//PartialStacking.csv";//activity.csv";//-third_1.csv";//-cereal.csv";//quarter.csv";//half.csv";
    //protected static String KBFile = "C:/Users/natalia/Dropbox/a-Evaluation PhD/Simulator/CAD-120fuzzyDLActivities_15.txt";//bare.txt";//5_1_datasetNames.txt";
    protected static String KBFileWithoutRules = workingPath.concat("CAD-120fuzzyDLActivities_15base.txt");
    protected static String outputRulesFile = workingPath.concat("GeneratedRules.txt");
    protected static String completeCrossValidationFile = workingPath.concat("SubactivityResuts_dist540_65test.csv"); //SubActivitiesAndObjectsWithUsersPerfectLabels.csv");
    protected static String resultsFile = workingPath.concat("ResultsFile.txt");
    protected static String statisticsFile = workingPath.concat("StatisticsFile.txt");
    protected static String resultsFilePrecRecallAccuracy = workingPath.concat("StatisticsFilePrecRecallAccuracy.txt");
    protected static String recognitionDurationsFile = workingPath.concat("RecognitionDurationsForActivity_"); // INSERT SPECIFIC ACTIVITY + .txt
    protected static String parseFileBeingCrossValidated;
    // 10 high-level activities: making cereal, taking medicine, stacking objects, unstacking objects, microwaving food, picking objects, cleaning objects, taking food, arranging objects, having a meal.
    //In practice: cereal, medicine, stacking, unstacking, microwave, bending, cleaning->cleaningObjects, take out, placing, eating.
    // 10 sub-activity labels: reaching, moving, pouring, eating, drinking, opening, placing, closing, scrubbing, null
    //In practice: cleaning, null, eating->eatingMeal?, scrubbing->cleaning->cleaningObjects, placing->?

    public static void main(String[] args) //throws FuzzyOntologyException,IOException, ParseException, InconsistentOntologyException
    {
        try {
            if (args.length>0){
                System.out.println("Running script for cross validation!");  // SCRIPT IS NON USED DUE TO FUZZYDL REQUIRING TO EMPTY EACH TIME WITH A NEW SETTING***
                /*if(args[0].equals("ConfigureActivities")){
                    System.out.println("Running ConfigureActivities");
                    configureActivitiesAndInitializeKB();  // VERY IMPORTANT: MUST BE CALLED FIRST OF ALL!
                }       else */
                if(args[0].contains("CrossValidate")){
                    String [] arguments = args[0].split("_");
                    int fold = Integer.parseInt(arguments[1]);
                    String trainingOrTest = arguments[2];
                    System.out.println("Running CrossValidate for fold " + fold + " " + trainingOrTest);
                    configureActivitiesAndInitializeKB();  // VERY IMPORTANT: MUST BE CALLED FIRST OF ALL!
                    crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, fold, workingPath.concat("TrainingFold4.csv"), workingPath.concat("CVTrainingFileKBFold4.txt")); //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 4, workingPath.concat("TrainingFold4PerfectLabels.csv"), workingPath.concat("CVTrainingFileKBFold4.txt"));
                }
                else{
                    if(args[0].equals("ComputeStatistics")){
                        System.out.println("Running computeFinalStatisticsFromResultsFileAndWriteToStatisticsFile");
                        configureActivitiesAndInitializeKB();  // VERY IMPORTANT: MUST BE CALLED FIRST OF ALL!
                        computeFinalStatisticsFromResultsFileAndWriteToStatisticsFile(resultsFile, resultsFilePrecRecallAccuracy, statisticsFile, usersIDs.size());
                    }
                    else {
                        System.out.println("Parameters for batch file are wrong!!");
                        System.exit(-1);
                    }
                }
            }

            /////////////////////////    START /////////////////////////////////////////////////////////////////////////////////////////////////////

            // STEP 1 (ALWAYS REQUIRED steps)
            configureActivitiesAndInitializeKB();
            //***************
            float[] result;
            // STEP 2
            //createCrossValidationKBfiles(usersIDs);
            // STEP 3
            //createCrossValidationInputFiles(completeCrossValidationFile, usersIDs);    //crossValidate(usersIDs, activityDetectionThresh

            // STEP 4
            //************* FOR PERFECT LABELS:
            //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 4, workingPath.concat("TrainingFold4PerfectLabels.csv"), workingPath.concat("CVTrainingFileKBFold4.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 4 + " gave accuracy: Training: " + result[1]+" "+result[2]+" "+result[3]);
            //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 4, workingPath.concat("TestFold4PerfectLabels.csv"), workingPath.concat("CVTestFileKBFold4.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 4 + " gave accuracy: Test: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 3,  workingPath.concat("TrainingFold3PerfectLabels.csv"), workingPath.concat("CVTrainingFileKBFold3.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 3 + " gave accuracy: Training: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 3,  workingPath.concat("TestFold3PerfectLabels.csv"), workingPath.concat("CVTestFileKBFold3.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 3 + " gave accuracy: Test: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 2,  workingPath.concat("TrainingFold2PerfectLabels.csv"), workingPath.concat("CVTrainingFileKBFold2.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 2 + " gave accuracy: Training: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 2, workingPath.concat("TestFold2PerfectLabels.csv"), workingPath.concat("CVTestFileKBFold2.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 2 + " gave accuracy: Test: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 1, workingPath.concat("TrainingFold1PerfectLabels.csv"), workingPath.concat("CVTrainingFileKBFold1.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 1 + " gave accuracy: Training: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 1, workingPath.concat("TestFold1PerfectLabels.csv"), workingPath.concat("CVTestFileKBFold1.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 1 + " gave accuracy: Test: "  + result[1]+" "+result[2]+" "+result[3]);

            // FOR PROPAGATED ERRORS FROM OLMO'S TRACKING AND RECOGNITION OF SUB-ACTIVITIES
            //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 4, workingPath.concat("TrainingFold4.csv"), workingPath.concat("CVTrainingFileKBFold4.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 4 + " gave accuracy: Training: " + result[1]+" "+result[2]+" "+result[3]);
            //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 4, workingPath.concat("TestFold4.csv"), workingPath.concat("CVTestFileKBFold4.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 4 + " gave accuracy: Test: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 3, workingPath.concat("TrainingFold3.csv"), workingPath.concat("CVTrainingFileKBFold3.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 3 + " gave accuracy: Training: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 3, workingPath.concat("TestFold3.csv"), workingPath.concat("CVTestFileKBFold3.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 3 + " gave accuracy: Test: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 2, workingPath.concat("TrainingFold2.csv"), workingPath.concat("CVTrainingFileKBFold2.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 2 + " gave accuracy: Training: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 2, workingPath.concat("TestFold2.csv"), workingPath.concat("CVTestFileKBFold2.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 2 + " gave accuracy: Test: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 1, workingPath.concat("TrainingFold1.csv"), workingPath.concat("CVTrainingFileKBFold1.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 1 + " gave accuracy: Training: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = crossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 1, workingPath.concat("TestFold1.csv"), workingPath.concat("CVTestFileKBFold1.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 1 + " gave accuracy: Test: "  + result[1]+" "+result[2]+" "+result[3]);


            // FOR NAIVE APPROACH WHERE ONLY FUZZYDL REASONER IS USED AND NON HEURISTIC FILTERS APPLIED (ONLY THE PREFILTER IS APPLIED)
            //result = naiveCrossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 4, workingPath.concat("TrainingFold4.csv"), workingPath.concat("CVTrainingFileKBFold4.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 4 + " gave accuracy: Training: " + result[1]+" "+result[2]+" "+result[3]);
            //result = naiveCrossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 4, workingPath.concat("TestFold4.csv"), workingPath.concat("CVTestFileKBFold4.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 4 + " gave accuracy: Test: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = naiveCrossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 3, workingPath.concat("TrainingFold3.csv"), workingPath.concat("CVTrainingFileKBFold3.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 3 + " gave accuracy: Training: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = naiveCrossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 3, workingPath.concat("TestFold3.csv"), workingPath.concat("CVTestFileKBFold3.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 3 + " gave accuracy: Test: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = naiveCrossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 2, workingPath.concat("TrainingFold2.csv"), workingPath.concat("CVTrainingFileKBFold2.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 2 + " gave accuracy: Training: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = naiveCrossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 2, workingPath.concat("TestFold2.csv"), workingPath.concat("CVTestFileKBFold2.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 2 + " gave accuracy: Test: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = naiveCrossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 1, workingPath.concat("TrainingFold1.csv"), workingPath.concat("CVTrainingFileKBFold1.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 1 + " gave accuracy: Training: "  + result[1]+" "+result[2]+" "+result[3]);
            //result = naiveCrossValidateFoldAndGetPrecisionRecallAndHitRate(usersIDs, activityDetectionThreshold, 1, workingPath.concat("TestFold1.csv"), workingPath.concat("CVTestFileKBFold1.txt"));
            //System.out.println("||||||||||||||||| Validation Fold " + 1 + " gave accuracy: Test: "  + result[1]+" "+result[2]+" "+result[3]);


            // STEP 5
             computeFinalStatisticsFromResultsFileAndWriteToStatisticsFile(resultsFile, resultsFilePrecRecallAccuracy, statisticsFile, usersIDs.size());

            //***************
            //float [] simulationResult;
            //simulationResult = SubActivityEventSimulator(testFile, activityDetectionThreshold);
            //System.out.println("Simulated " + simulationResult[0] + " Activities in time with detection threshold "+activityDetectionThreshold);
            //System.out.println("Precision " + simulationResult[1] + " Recall "+simulationResult[2]);
            //System.out.println("Simulated "+ SubActivityEventSimulator(testFile)+" Activities in time");

            System.out.println("End");
            //******************************************************
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in main program: " + e.getMessage());
        }
    }

    protected static void computeFinalStatisticsFromResultsFileAndWriteToStatisticsFile(String resultsFile, String resultsPrecRecAccFile, String statisticsFile, int folds) throws IOException, ParseException{

        float[][] testSumMatrix = new float[activities.size()][activities.size() + 1];
        float[][] trainSumMatrix = new float[activities.size()][activities.size() + 1];
        float[][] testOverallMatrix = new float[activities.size()][activities.size() + 1];
        float[][] trainOverallMatrix = new float[activities.size()][activities.size() + 1];

        float[][] generalSumMatrix = new float[activities.size()][activities.size() + 1];
        ArrayList<float[][]> arrayOfTrainingMatrices = new ArrayList<float[][]>();
        ArrayList<float[][]> arrayOfTestMatrices = new ArrayList<float[][]>();

        FileInputStream fstream = new FileInputStream(resultsFile);       //"dataset.txt"
        // Get the object of DataInputStream
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        int matricesRead = 0; // up to folds*2;
        // Matrix initialization;
        for(int i=0; i< trainSumMatrix.length; ++i)
            for(int j=0; j<trainSumMatrix[i].length; ++j){
                trainSumMatrix[i][j] = 0f;
                testSumMatrix[i][j] = 0f;
                trainOverallMatrix[i][j] = 0f;
                testOverallMatrix[i][j] = 0f;
                generalSumMatrix[i][j] = 0f;
            }

        int activ = 0; // row we are reading and filling data in
        System.out.println("------  Train and Test matrices");
        try{
            // read from file and load train and test matrices to sum all values
            while ((strLine = br.readLine()) != null){ //for each sub-activity register
                //while(f<matricesToCount){
                String[] activityRow = strLine.split(",");
                // Add results to trainSumMatrix 1st and testSumMatrix 2st
                if(!strLine.equals("")){//!strLine.trim().isEmpty()){
                    if((matricesRead % 2) ==0){ // Train
                        for(int j=0; j<activityRow.length-1; ++j){ // because there is a comma after the last value
                            if(!activityRow[j].equals("")){
                                trainSumMatrix[activ][j] = Float.parseFloat(activityRow[j]);
                                //activ++;
                            }
                        }
                        activ++;
                    }
                    else{ // Test
                        //for(int i=0; i<trainSumMatrix.length; ++i)
                        for(int j=0; j<activityRow.length-1; ++j){
                            if(!activityRow[j].equals("")){
                                testSumMatrix[activ][j] = Float.parseFloat(activityRow[j]);
                                //activ++;
                            }
                        }
                        activ++;
                    }
                }
                else{ // Empty line, separation to indicate new matrix
                    if(matricesRead % 2 == 0){ // TRAIN
                        System.out.println("Train matrix -nr: "+matricesRead);
                        printMatrix(trainSumMatrix);
                        //if(!arrayOfTrainingMatrices.contains(trainSumMatrix))
                            arrayOfTrainingMatrices.add(trainSumMatrix);
                    }
                    else{// TEST
                        System.out.println("Test matrix -nr: "+matricesRead);
                        printMatrix(testSumMatrix);
                        //if(!arrayOfTestMatrices.contains(testSumMatrix))
                            arrayOfTestMatrices.add(testSumMatrix);
                    }
                    matricesRead++;
                    activ = 0;
                }
            }

            //Close the input stream
            in.close();

            for(int i=0; i< arrayOfTestMatrices.size(); ++i)
                for(int j=0; j< arrayOfTestMatrices.get(i).length; ++j)
                    for(int k=0; k< arrayOfTestMatrices.get(i)[j].length; ++k){
                        trainOverallMatrix[j][k] += arrayOfTrainingMatrices.get(i)[j][k];
                        testOverallMatrix[j][k] += arrayOfTestMatrices.get(i)[j][k];
                    }

            // Dividing by the number of folds
            for(int i=0; i<trainSumMatrix.length; ++i)
                for(int j=0; j<trainSumMatrix[i].length; ++j){
                    testOverallMatrix[i][j] = testOverallMatrix[i][j] / folds; // TODO: check that rounding is done properly
                    trainOverallMatrix[i][j] = trainOverallMatrix[i][j] / folds;
                }

            for(int i=0; i<trainSumMatrix.length; ++i)
                for(int j=0; j<trainSumMatrix[i].length; ++j)
                    generalSumMatrix[i][j] += round(((0.25* (testOverallMatrix[i][j])) + (0.75* (trainOverallMatrix[i][j]))), 3);

            System.out.println("\n\nOverall train, test  and general confusion matrices: ");
            printMatrix(trainOverallMatrix);
            System.out.println();
            printMatrix(testOverallMatrix);
            System.out.println();
            printMatrix(generalSumMatrix);

            writeToFile(statisticsFile, "\n\n".concat(new Date().toString()).concat("\n"));
            writeToFileMatrix(statisticsFile, trainOverallMatrix);
            writeToFileMatrix(statisticsFile, testOverallMatrix);
            writeToFileMatrix(statisticsFile, generalSumMatrix);

            System.out.println("Precision Matrices appended to file: "+statisticsFile+"!");

            // **************************
            // Averaging and summarizing prec, recall and accuracy
            FileInputStream fstream2 = new FileInputStream(resultsPrecRecAccFile);       //"dataset.txt"
            // Get the object of DataInputStream
            DataInputStream in2 = new DataInputStream(fstream2);
            BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));
            String strLine2;
            ArrayList<Float> precisionsTrain = new ArrayList<Float>();
            ArrayList<Float> recallsTrain = new ArrayList<Float>();
            ArrayList<Float> accuraciesTrain = new ArrayList<Float>();
            ArrayList<Float> precisionsTest = new ArrayList<Float>();
            ArrayList<Float> recallsTest = new ArrayList<Float>();
            ArrayList<Float> accuraciesTest = new ArrayList<Float>();
            int line = 0;
            try{
                // read from file and load train and test matrices to sum all values
                while ((strLine2 = br2.readLine()) != null){ //for each sub-activity register
                    String[] fold = strLine2.split(",");
                    if(!fold[0].equals("")){
                        if(line %2 ==0){
                            precisionsTrain.add(Float.parseFloat(fold[0]));
                            recallsTrain.add(Float.parseFloat(fold[1]));
                            accuraciesTrain.add(Float.parseFloat(fold[2]));
                        }
                        else{
                            precisionsTest.add(Float.parseFloat(fold[0]));
                            recallsTest.add(Float.parseFloat(fold[1]));
                            accuraciesTest.add(Float.parseFloat(fold[2]));
                        }
                        line++;
                    }
                }
                float precSumTrain = 0f;
                float recSumTrain = 0f;
                float accSumTrain = 0f;
                // averaging Training results
                for(int i=0; i<precisionsTrain.size(); ++i){
                    precSumTrain += precisionsTrain.get(i);
                    recSumTrain += recallsTrain.get(i);
                    accSumTrain += accuraciesTrain.get(i);
                }
                precSumTrain = precSumTrain/ precisionsTrain.size();
                recSumTrain = recSumTrain/ recallsTrain.size();
                accSumTrain = accSumTrain/ accuraciesTrain.size();


                // averaging Test results
                float precSumTest = 0f;
                float recSumTest = 0f;
                float accSumTest = 0f;
                for(int i=0; i<precisionsTest.size(); ++i){
                    precSumTest += precisionsTest.get(i);
                    recSumTest += recallsTest.get(i);
                    accSumTest += accuraciesTest.get(i);
                }
                precSumTest = precSumTest/ precisionsTest.size();
                recSumTest = recSumTest/ recallsTest.size();
                accSumTest = accSumTest/ accuraciesTest.size();

                //Close the input stream
                in2.close();

                float finalPrec = round((float) ((0.25 * precSumTest) + (0.75 * precSumTrain)), 3);
                float finalRec = round((float) ((0.25 * recSumTest) + (0.75 * recSumTrain)), 3);
                float finalAcc = round((float) ((0.25 * accSumTest) + (0.75 * accSumTrain)), 3);
                System.out.println("Final precision, recall and acc.: "+ finalPrec+"  "+finalRec+"  "+ finalAcc);

                writeToFile(statisticsFile, "\nPrecision: " + Float.toString(finalPrec) + " Recall: " + Float.toString(finalRec) + " Accuracy: " + Float.toString(finalAcc) + " \n");
            }
            catch (Exception e){//Catch exception if any
                System.err.println("Error in computeFinalStatisticsFromResultsFileAndWriteToStatisticsFile writing to file: "+resultsPrecRecAccFile+" :" + e.getMessage());
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in computeFinalStatisticsFromResultsFileAndWriteToStatisticsFile: " + e.getMessage());
        }
    }

    protected static void createCrossValidationKBfiles(ArrayList<Integer> usersIDs) throws IOException, ParseException{
        try{
            // = UserIDs: 5, 4, 3, 1 in CAD-120
            int fold = 1;
            for(int i=0; i<usersIDs.size(); ++i){
                // Learn rules and write them to file
                String CVTrainingKBFile = workingPath.concat("CVTrainingFileKBFold"+fold+".txt");
                String CVTestKBFile = workingPath.concat("CVTestFileKBFold"+fold+".txt");
                String learntRulesTest = LearnWeightsAndCreateRules(trainingFileWithUsers, new ArrayList<Integer>(Arrays.asList(usersIDs.get(i))), CVTestKBFile);
                ArrayList<Integer> kFoldsIDsCopy = (ArrayList<Integer>)usersIDs.clone();
                kFoldsIDsCopy.remove(usersIDs.get(i));
                String learntRulesTraining = LearnWeightsAndCreateRules(trainingFileWithUsers, kFoldsIDsCopy, CVTrainingKBFile);
                System.out.println("Cross-validation "+fold+ " Learnt rules training: \n"+ learntRulesTraining);
                System.out.println("Cross-validation "+fold+ " Learnt rules testing: \n"+ learntRulesTest);
                fold++;
            }
            System.out.println("KB and rule files for cross-validation created!");
        }catch (Exception e){//Catch exception if any
            System.err.println("createCrossValidationKBfiles: " + e.getMessage());
        }
    }

    protected static void createCrossValidationInputFiles(String completeFile, ArrayList<Integer> usersIDs) throws IOException, ParseException{
        try{
            // = UserIDs: 5, 4, 3, 1 in CAD-120
            for(int i=0; i<usersIDs.size(); ++i){
                // write to file corresponding registers according to Users: contentForTesting and contentForTraining
                if(!completeFile.contains("PerfectLabels")){
                    String CVTrainingFile = workingPath.concat("TrainingFold"+(i+1)+".csv");
                    String CVTestFile = workingPath.concat("TestFold"+(i+1)+".csv");
                    readAndSplitTextFileFilteringXHighestCertSubAct(completeFile, (i+1), CVTrainingFile, CVTestFile, 1);
                }
                else{
                    String CVTrainingFile = workingPath.concat("TrainingFold"+(i+1)+"PerfectLabels.csv");
                    String CVTestFile = workingPath.concat("TestFold"+(i+1)+"PerfectLabels.csv");
                    readAndSplitTextFileForFoldID(completeFile, usersIDs.get(i), CVTrainingFile, CVTestFile);
                }
            }
            System.out.println("Cross validation Input Files created!");
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in createCrossValidationInputFiles: " + e.getMessage());
        }
    }

    protected static float[] crossValidateFoldAndGetPrecisionRecallAndHitRate(ArrayList<Integer> usersIDs, float activityDetectionThreshold, int fold, String CVInputFile, String KBFile) throws FuzzyOntologyException,IOException, ParseException{
        float[] result = new float[]{};
        try{
             // = UserIDs: 5, 4, 3, 1 in CAD-120
            System.out.println("Cross validating with input file: " + CVInputFile+" and KB file: "+KBFile);
            result = validateFold(fold, CVInputFile, activityDetectionThreshold, KBFile);
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in crossValidateFold: " + e.getMessage());
        }
        return result;
    }

    protected static float[] naiveCrossValidateFoldAndGetPrecisionRecallAndHitRate(ArrayList<Integer> usersIDs, float activityDetectionThreshold, int fold, String CVInputFile, String KBFile) throws FuzzyOntologyException,IOException, ParseException{
        float[] result = new float[]{};
        try{
            // = UserIDs: 5, 4, 3, 1 in CAD-120
            System.out.println("Cross validating with input file: " + CVInputFile+" and KB file: "+KBFile);
            result = naiveValidateFold(fold, CVInputFile, activityDetectionThreshold, KBFile);
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in naiveCrossValidateFoldAndGetPrecisionRecallAndHitRate: " + e.getMessage());
        }
        return result;
    }

    /*protected static void crossValidateFold(ArrayList<Integer> usersIDs, float activityDetectionThreshold, int fold) throws FuzzyOntologyException,IOException, ParseException{
        try{
            // = UserIDs: 5, 4, 3, 1 in CAD-120
            String CVTrainingFile = workingPath.concat("TrainingFold"+fold+".csv");
            String CVTestFile = workingPath.concat("TestFold"+fold+".csv");
            float resultsTrain = validateFoldTrainingOrTest(fold, CVTrainingFile, activityDetectionThreshold, "training");
            float resultsTest = validateFoldTrainingOrTest(fold, CVTestFile, activityDetectionThreshold, "test");
            float hitRateTotal = (float)((0.75*resultsTrain)+(0.25*resultsTest));
            System.out.println("||||||||||||||||| Validation Fold " + fold + " gave accuracy: Training: " + resultsTrain + " Test: " + resultsTest + " Total: " + hitRateTotal);
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in crossValidateFold: " + e.getMessage());
        }
    }*/



    /*protected static ArrayList<Float> validateFold(int fold, String trainingFile, String testFile, float activityDetectThreshold){
        System.out.println("CROSS VALIDATING FOLD "+fold);// TEST
        //setParserWithFile(workingPath.concat("CVTestFileKBFold"+fold+".txt"));
        //parser = null;
        parseFileBeingCrossValidated = workingPath.concat("CVTestFileKBFold"+fold+".txt");
        //emptyAndRestartKB();
        setParserWithFileAndEmptyKB(parseFileBeingCrossValidated);
        float hitRateTest = SubActivityEventSimulator(testFile, activityDetectThreshold)[3];

        // TRAINING
        //setParserWithFile(workingPath.concat("CVTrainingFileKBFold"+fold+".txt"));
        //parser = null;
        parseFileBeingCrossValidated = workingPath.concat("CVTrainingFileKBFold"+fold+".txt");
        //emptyAndRestartKB();
        setParserWithFileAndEmptyKB(parseFileBeingCrossValidated);
        float hitRateTraining = SubActivityEventSimulator(trainingFile, activityDetectThreshold)[3];

        float hitRateTotal = (float)((0.75*hitRateTraining)+(0.25*hitRateTest));
        return new ArrayList<Float>(Arrays.asList(hitRateTraining, hitRateTest, hitRateTotal));
    }*/

    protected static float[] validateFold(int fold, String inputDataFile, float activityDetectThreshold, String KBFile) throws InconsistentOntologyException{
        float [] precisionRecallAndHitRate = new float[]{};
        try{
            System.out.println("CROSS VALIDATING FOLD "+fold);// TEST
            initializeKB(individualName);
            // TRAINING
            //setParserWithFile(workingPath.concat("CVTrainingFileKBFold"+fold+".txt"));
            //parser = null;
            //emptyAndRestartKB();
            setParserWithFileAndEmptyKB(KBFile);
            precisionRecallAndHitRate = SubActivityEventSimulator(inputDataFile, activityDetectThreshold);

        }catch (Exception e){//Catch exception if any
            System.err.println("Error in validateFold: " + e.getMessage());
        }
        return precisionRecallAndHitRate;
    }

    protected static float[] naiveValidateFold(int fold, String inputDataFile, float activityDetectThreshold, String KBFile) throws InconsistentOntologyException{
        float [] precisionRecallAndHitRate = new float[]{};
        try{
            System.out.println("CROSS VALIDATING FOLD "+fold);// TEST
            initializeKB(individualName);
            // TRAINING
            //setParserWithFile(workingPath.concat("CVTrainingFileKBFold"+fold+".txt"));
            //parser = null;
            //emptyAndRestartKB();
            setParserWithFileAndEmptyKB(KBFile);
            precisionRecallAndHitRate = naiveSubActivityEventSimulator(inputDataFile, activityDetectThreshold);

        }catch (Exception e){//Catch exception if any
            System.err.println("Error in naiveValidateFold: " + e.getMessage());
        }
        return precisionRecallAndHitRate;
    }


    public static void readAndSplitTextFileForFoldID(String fileName, int fold, String outputFileNameTraining, String outputFileNameTest) {
        FileReader file;
        String line = "";
        try {
            file = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(file);
            try {
                while ((line = reader.readLine()) != null) {
                    String[] register = line.split(",");
                    if(!register[0].equals("\"fold\"")){ // Header
                        // Removing "" quotes from register data
                        for (int j=0; j<register.length; ++j){
                            register[j] = removeQuotes(register[j]);
                        }
                        if (register[1].equals("eating")) register[1] = "eatingMeal";
                        if (register[1].equals("cleaning")) register[1] = "cleaningObjects";
                        if (register[1].equals("placing")) register[1] = "arrangingObjects";
                        if (register[1].equals("microwave")) register[1] = "microwaving";
                        if(Integer.parseInt(register[0])== fold)  // Write to files  // TRAINING
                            writeToFile(outputFileNameTest, line.concat("\n"));
                        else // TEST
                            writeToFile(outputFileNameTraining, line.concat("\n"));
                    }
                }
            } finally {
                reader.close();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found in readAndSplitTextFileForFoldID");
        } catch (IOException e) {
            throw new RuntimeException("IO Error occurred in readAndSplitTextFileForFoldID");
        }
    }

    public static void writeConfusionMatrixToResultsFile(String fileName, float[][] confusionMatrix) {
        FileReader file;
        String lineToWrite = "";
        try{
            for(int i=0; i<confusionMatrix.length; ++i){
                lineToWrite= "";
                for(int j=0; j< confusionMatrix[i].length; ++j)
                    lineToWrite += (Float.toString(confusionMatrix[i][j]).concat(" , "));
                //System.out.println("Writing line: "+lineToWrite.concat("\n"));
                writeToFile(fileName, lineToWrite.concat("\n"));
            }
            lineToWrite = "\n"; // separation line
            writeToFile(fileName, lineToWrite);
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in addConfusionMatrixToResultsFile: " + e.getMessage());
        }
    }

    public static void readAndSplitTextFileFilteringXHighestCertSubAct(String fileName, int fold, String outputFileNameTraining, String outputFileNameTest, int x) {
        FileReader file;
        String line = "";
        try{
            try {
                file = new FileReader(fileName);
                BufferedReader reader = new BufferedReader(file);
                int startFrame = -1;
                ArrayList<String> subActivitiesOccurredInInterval = new ArrayList<String>();
                ArrayList<String> registriesOccurredInInterval = new ArrayList<String>();
                ArrayList<Float> subActivitiesDistCertainties = new ArrayList<Float>();
                try {
                    while ((line = reader.readLine()) != null) {
                        String[] register = line.split(",");
                        // Removing "" quotes from register data
                        for (int j=0; j<register.length; ++j)
                            register[j] = removeQuotes(register[j]);
                        if(!register[0].equals("fold")){ // Header
                            if(Integer.parseInt(register[0])== fold){// && Float.parseFloat(register[7]) > minSubActDetectionThreshold){ // If certainty is big enough to recognize SA, we take it
                                //Collect all probable SA for the given time interval
                                if (startFrame != -1 ){
                                    if (startFrame != Integer.parseInt(register[8])){ // NEW SUBACTIVITY BEING SIMULATED       // TODO: check if more than one activity is detected with same possibility.
                                        // Select the N SubActivities with highest distance_certainty
                                        //ArrayList<String> filteredRegistriesToAdd = new ArrayList<String>();
                                        int s=startFrame;
                                        String selectedSubAct = getMaxDegreeActivity(subActivitiesDistCertainties, subActivitiesOccurredInInterval); // TODO use x
                                        // Write to files
                                        for(int reg=0; reg<registriesOccurredInInterval.size(); ++reg){
                                            String [] r = registriesOccurredInInterval.get(reg).split(",");
                                            for (int j=0; j<r.length; ++j)
                                                r[j] = removeQuotes(r[j]);
                                            if(r[6].equals(selectedSubAct)){
                                                if(Integer.parseInt(r[1]) == 1) //  1= Train
                                                    writeToFile(outputFileNameTraining, registriesOccurredInInterval.get(reg).concat("\n"));
                                                else{
                                                    if(Integer.parseInt(r[1]) == 2)// 2= Test
                                                        writeToFile(outputFileNameTest, registriesOccurredInInterval.get(reg).concat("\n"));
                                                    else{
                                                        System.out.println(" ERROR IN CROSS-VALIDATION FILE KEYS");
                                                        System.exit(-1);
                                                    }
                                                }
                                            }
                                        }
                                        registriesOccurredInInterval.clear();
                                        subActivitiesOccurredInInterval.clear();
                                        subActivitiesDistCertainties.clear();

                                        startFrame = Integer.parseInt(register[8]);
                                        registriesOccurredInInterval.add(line);
                                        if(!subActivitiesOccurredInInterval.contains(register[6])){
                                            subActivitiesOccurredInInterval.add(register[6]);
                                            subActivitiesDistCertainties.add(round(Float.parseFloat(register[7]), 2));
                                        }
                                    }
                                    else{ // Same subActivity, already started, running
                                        startFrame = Integer.parseInt(register[8]);
                                        registriesOccurredInInterval.add(line);
                                        if(!subActivitiesOccurredInInterval.contains(register[6])){
                                            subActivitiesOccurredInInterval.add(register[6]);
                                            subActivitiesDistCertainties.add(round(Float.parseFloat(register[7]), 2));
                                        }
                                    }
                                }
                                else { //First, initialization step (First SubActivity)
                                    startFrame = Integer.parseInt(register[8]);
                                    registriesOccurredInInterval.add(line);
                                    if(!subActivitiesOccurredInInterval.contains(register[6])){
                                        subActivitiesOccurredInInterval.add(register[6]);
                                        subActivitiesDistCertainties.add(round(Float.parseFloat(register[7]), 2));
                                    }
                                    //endFrame = Integer.parseInt(register[9]);
                                }
                            }
                        }
                    }
                } finally {
                    reader.close();
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException("File not found");
            } catch (IOException e) {
                throw new RuntimeException("IO Error occurred");
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in readAndSplitTextFileFilteringXHighestCertSubAct: " + e.getMessage());
        }
    }

    public static String readTextFile(String fileName) {
        String returnValue = "";
        FileReader file;
        String line = "";
        try {
            file = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(file);
            try {
                while ((line = reader.readLine()) != null) {
                    returnValue += line + "\n";
                }
            } finally {
                reader.close();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        } catch (IOException e) {
            throw new RuntimeException("IO Error occurred");
        }
        return returnValue;
    }

    public static void writeToFile(String fileName, String s) {
        FileWriter output;
        try {
            output = new FileWriter(fileName, true); // second parameter: appends at the end
            BufferedWriter writer = new BufferedWriter(output);
            writer.write(s);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFileMatrix(String fileName, float[][] m) {
        FileWriter output;
        try {
            String line = "";
            output = new FileWriter(fileName, true); // second parameter: appends at the end
            BufferedWriter writer = new BufferedWriter(output);
            for (int i=0; i< m.length; ++i){
                line = "";
                for(int j=0; j< m[i].length; ++j)
                    line += Float.toString(m[i][j]).concat(", ");
                writer.write(line);
                writer.write("\n");
            }
            writer.write("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static float getCertaintyOfActivityHappeningFDL( String activityConcept) throws InconsistentOntologyException{//String subActivityIndividual, String dataPropertyName){  //String subActivityIndividualName,
        float certainty = 0f;
        try{
            //Date cycleStartTime = new Date();

            // EMPTYING AND FILLING KB FIRST
            emptyAndRestartKB();
            ArrayList<Dictionary> activitiesOccurredInLastFTW = cutFTWInNrOfSubActFromLatestSubActivitiesForActivity(activityConcept);
            insertFTWAxiomsIntoKB(activitiesOccurredInLastFTW);
            // QUERYING FOR ACTIVITY HAPPENING    -equivalent query to  (min-instance? Natalia (some performsActivity cereal)):
            kb.solveKB(); // TODO: WORKS WITHOUT IT?!
            Concept activityC = new Concept(activityConcept);
            Concept conceptSome = Concept.some("performsActivity", activityC);
            MinInstanceQuery q = new MinInstanceQuery(conceptSome, userIndividual);
            startQuerySolution = q.solve(kb);
            certainty = round((float)startQuerySolution.getSolution(), 2);

            // Compute duration of query time
            // Date endTime = new Date(); // pre-set to 1h (max of the avg times of each activity type)
            //System.out.print("QueryTime: "+(endTime.getTime() - cycleStartTime.getTime())+" "); // TODO: query time calculate avg

            //System.out.println("Activity "+ activityConcept+ " MinRelatedQuery: "+certainty);
        } catch (Exception e){//FuzzyOntologyException e) {
            System.err.println("Error in getCertaintyOfActivityHappeningFDL: " + e.getMessage());
        }
        return  certainty;
    }

    public static float[] naiveSubActivityEventSimulator(String file, float activDetectionThreshold){
        // THIS SIMULATOR DIFFERS WITH THE SubActivityEventSimulator in the fact that the naive one does not apply heuristics filters and uniquely the fuzzyDL reasoner

        // E.g.: 126141638,"cereal","988","moving","50","105","53","2","box","516.5936279296875","661.789306640625","364.231 305.74 114.842"
        // ActID, ActName, SubActID, SubActName, SubActStartFrame, SubActEndFrame, FrameNr, ObjectID, ObjectName, DistanceRight, DistanceLeft, Pos3D
        // Idact.frame.objId.distLeft.distRight.objName.pos3D Distance is in cm. Touching happens when smaller than max. ~52cm.
        // Input event is considered all the time; however, the object is annotated only when the distance to any of the objects is closer than 49cm. Otherwise it is "".
        int nActivities = 0;
        int objectEvents = 0;
        nSubActivitiesDetected = 0;
        realFrameClock = 0;
        int sumRightGuesses = 0;
        float[] results = new float[4]; // N. of activities simulated, precision and recall of the simulation experiment
        ActivitiesCertaintiesPairs secondRatioActivitiesCertaintiesPairs = new ActivitiesCertaintiesPairs(new ArrayList<String>(), new ArrayList<Float>());
        // format example: 126141638,"cereal","988","moving","50","105"
        try{
            FileInputStream fstream = new FileInputStream(file);       //"dataset.txt"
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            //Confusion matrix for high-level activity labeling: Initialization
            int [][] classification = new int[activities.size()][activities.size()+1];
            for (int i=0; i< classification.length; ++i)
                for (int j =0; j<classification[i].length; j++)
                    classification[i][j] = 0;
            float [][] statisticsTP_TN_FP_FN = new float[activities.size()][6];
            for (int i=0; i< statisticsTP_TN_FP_FN.length; ++i)
                for (int j =0; j<statisticsTP_TN_FP_FN[i].length; j++)
                    statisticsTP_TN_FP_FN[i][j] = 0;

            String activ = ""; // FOR SIMULATION PURPOSES
            String activityID = "";
            ArrayList<String> detectedActivities = new ArrayList<String>();
            ArrayList<String> detectedActivities1 = new ArrayList<String>();
            int previousEndFrame = 0;
            int currentStartFrame = -1;
            int currentEndFrame = -1;
            int outputStartFrame = -1;
            int outputEndFrame = -1;
            String previousSubActivity = "";
            // FOR FILE CROSS VALIDATION PURPOSES
            //5,"126141638","cereal","988","moving","50","105","50","1","bowl","136.4384307861328","147.10508728027344","-20.406 684.239 331.827"
            int UserID, StartFrame, EndFrame, CurrentFrame;
            String SA = "";
            String Activity = "";
            String Object = "";
            String ActivityID, SA_ID, ObjectID;
            float LeftHandDistance = 0;
            float RightHandDistance = 0;
            float ObjectPosX = 0;
            float ObjectPosY = 0;
            float ObjectPosZ = 0;
            float [] ObjectPosition = new float[3];
            Double SA_certainty = 0.0;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null){ //for each sub-activity register
                String[] register = strLine.split(",");
                for (int i=0; i<register.length; ++i)
                    register[i] = removeQuotes(register[i]);
                if(file.contains("PerfectLabels")){ // WITH PERFECT DATASET LABELS FOR EACH SUB-ACTIVITY DETECTED
                    UserID= Integer.parseInt(register[0]);
                    ActivityID = register[1];
                    Activity = register[2];
                    SA_ID = register[3];
                    SA = register[4];
                    StartFrame = Integer.parseInt(register[5]);
                    EndFrame = Integer.parseInt(register[6]);
                    CurrentFrame = Integer.parseInt(register[7]);
                    ObjectID = register[8];
                    Object = register[9];
                    RightHandDistance = Math.abs(round(Float.parseFloat(register[10]), 2));
                    LeftHandDistance = Math.abs(round(Float.parseFloat(register[11]), 2));
                    String [] positions = register[12].split(" ");
                    ObjectPosX = round(Float.parseFloat(positions[0]),2);
                    ObjectPosY = round(Float.parseFloat(positions[1]), 2);
                    ObjectPosZ = round(Float.parseFloat(positions[2]), 2);
                    ObjectPosition = new float[]{ObjectPosX, ObjectPosY, ObjectPosZ};
                    SA_certainty = defaultDegree;
                }
                else{ // WITH ERROR PROPAGATION FROM SUB-ACTIVITIES TRACKED AND DETECTED BY OLMO
                    //fold,"train_test_type","id","name","subact_id_real","name","subact_name_detected","distance_grade","start_frame","end_frame","frame","object_id","object_name","distance_left","distance_right","position"
                    //1,"1","126141638","cereal","988","moving","cleaning","0.17738458476960717","50","105","1","1","bowl","147.10508728027344","136.4384307861328","-20.406 684.239 331.827"
                    ActivityID = register[2];
                    Activity = register[3];
                    SA_ID = register[4];
                    SA = register[6];
                    StartFrame = Integer.parseInt(register[8]);
                    EndFrame = Integer.parseInt(register[9]);
                    CurrentFrame = Integer.parseInt(register[10]);
                    ObjectID = register[11];
                    Object = register[12];
                    RightHandDistance = Math.abs(round(Float.parseFloat(register[13]), 2));
                    LeftHandDistance = Math.abs(round(Float.parseFloat(register[14]), 2));
                    String [] positions = register[15].split(" ");
                    ObjectPosX = round(Float.parseFloat(positions[0]), 2);
                    ObjectPosY = round(Float.parseFloat(positions[1]), 2);
                    ObjectPosZ = round(Float.parseFloat(positions[2]), 2);
                    ObjectPosition = new float[]{ObjectPosX, ObjectPosY, ObjectPosZ};
                    SA_certainty = round(Double.parseDouble(register[7]), 2);
                }

                if (Activity.equals("eating")) Activity = "eatingMeal";
                if (Activity.equals("cleaning")) Activity = "cleaningObjects";
                if (Activity.equals("placing")) Activity = "arrangingObjects";
                if (Activity.equals("microwave")) Activity = "microwaving";
                if (Object.equals("NULL")) Object = NULL_OBJECT;
                if (!activityID.equals("") ){
                    if(!previousSubActivity.equals(SA))
                        nSubActivitiesDetected++;
                    //System.out.println("NSubActivities "+nSubActivities+". Size of KBmirrorSubAct "+KBmirrorSubActivities.size());
                    if (!ActivityID.equals(activityID)){ // NEW ACTIVITY BEING SIMULATED       // TODO: check if more than one activity is detected with same possibility.
                        // Start counting AVG ACTIVITY RECOGNITION TIME:
                        Date activRecogStartTime = new Date();

                        // STEP 0 -PRE-FILTER: RATIO OF SA-OBJECTS OCCURRENCES
                        System.out.print("\n- Activity " + activ + ". Ratio pre-filter passed by: ");
                        detectedActivities.clear();
                        ActivitiesCertaintiesPairs preRatioActivitiesCertaintiesPairs = filterActivitiesContainingSubActivityAndObjectConstraints();
                        detectedActivities = preRatioActivitiesCertaintiesPairs.getActivities();
                        //for (int i=0; i<detectedActivities.size(); i++){  // RATIO PRE-FILTER
                        //  System.out.print(detectedActivities.get(i)+ " - ");
                        //}

                        // STEP 3 - FINAL FILTER: DEGREE OF CERTAINTY ASSESSMENT
                        activCertainties.clear();
                        candidateActivities.clear();
                        System.out.print("\n- Activity "+activ+". Certainties of candidate activities: ");//All constraints passed by: ");
                        for (int i=0; i<detectedActivities.size(); i++){  // OBJECT POSITIONS
                            activCertainties.add(getCertaintyOfActivityHappeningFDL(detectedActivities.get(i))); // BIGGEST RUNNING TIME OVERHEAD: deleting KB, inserting axioms and querying.
                            candidateActivities.add(detectedActivities.get(i));
                            System.out.print(candidateActivities.get(i)+ "("+activCertainties.get(i) + ") ");
                        }
                        // CLASSIFICATION RESULTS
                        if (candidateActivities.size()< 1){
                            classification[activities.get(activ)][activities.size()]+= 1; // Filling the null (last) column
                            System.out.println( "   -"+nActivities+ " "+activityID+" -> Detected as NULL ____________________________________ ");
                        }
                        else{ // OBJECT POSITIONS / HIGHEST CERTAINTY    //FILTRAR LA DE MAYOR UMBRAL- ASIGNAR UMBRAL A PARTIR DEL NUMERO De PATRONES CUMPLIDOS/INTERACCIONES CON OBJETOS Y RESTRICCIONES DE OBJECT POS.
                            String detectedActivity = getNaiveActivityWithHighestCertaintyOverThreshold(activCertainties, candidateActivities, activDetectionThreshold, secondRatioActivitiesCertaintiesPairs, preRatioActivitiesCertaintiesPairs); // Gives "" for cereal!
                            if(!detectedActivity.equals("")){ // if activity involves SubActivities of stackable, check their objects positions
                                classification[activities.get(activ)][activities.get(detectedActivity)]+= 1;
                                if(detectedActivity.equals(activ)){
                                    sumRightGuesses++;
                                    System.out.println("  ------------------------------------------ BINGO: " + activ );
                                }
                                else
                                    System.out.println("   -"+nActivities+ " "+activityID+" -> Detected as: "+ detectedActivity);
                            }
                            else{
                                System.out.println( "   -"+nActivities+ " "+activityID+" -> Detected as NULL ____________________________________ ");
                                classification[activities.get(activ)][activities.size()]+= 1; // NULL
                            }
                        }
                        // Compute duration of query + recognition time getTime() returns milliseconds
                        Date endTime = new Date(); // pre-set to 1h (max of the avg times of each activity type)
                        //System.out.print("QueryTime: "+(endTime.getTime() - activRecogStartTime.getTime())+" ");
                        writeToFile(recognitionDurationsFile+activ+".txt", Float.toString(endTime.getTime() - activRecogStartTime.getTime()).concat("\n"));

                        nActivities++;
                        objectEvents++;
                        activityID = ActivityID;
                        activ = Activity;

                        currentStartFrame = StartFrame;
                        currentEndFrame = EndFrame;
                        realFrameClock += previousEndFrame;
                        outputStartFrame = realFrameClock + currentStartFrame;
                        outputEndFrame = realFrameClock + currentEndFrame;
                        previousEndFrame = currentEndFrame;
                        //if(SA_certainty> minSubActDetectionThreshold)
                        onNewEvent(SA_ID, SA, SA_certainty, outputStartFrame, outputEndFrame, Object, ObjectID, ObjectPosition, new float[]{RightHandDistance, LeftHandDistance}); // TODO: Fix: 2 objects needed?
                        previousSubActivity = SA; //System.out.println("Returned "+predictedActivities.size()+" predicted activities");

                    }
                    else{ // Same activity, already started, running
                        activ = Activity;
                        activityID = ActivityID;
                        currentStartFrame = StartFrame;
                        currentEndFrame = EndFrame;
                        outputStartFrame = realFrameClock + currentStartFrame;
                        outputEndFrame = realFrameClock + currentEndFrame;
                        previousEndFrame = currentEndFrame;
                        //if(SA_certainty> minSubActDetectionThreshold)
                        onNewEvent(SA_ID, SA, SA_certainty, outputStartFrame, outputEndFrame, Object, ObjectID, ObjectPosition, new float[]{RightHandDistance, LeftHandDistance}); // TODO: Fix: 2 objects needed?
                        objectEvents++;
                        previousSubActivity = SA;
                    }
                }
                else { //First, initialization step (First SubActivity)
                    activityID = ActivityID;
                    activ = Activity;
                    outputStartFrame = (realFrameClock+ (StartFrame-realFrameClock));
                    outputEndFrame = (outputStartFrame+(EndFrame - StartFrame));
                    previousEndFrame = outputEndFrame;
                    //if(SA_certainty> minSubActDetectionThreshold)
                    onNewEvent(SA_ID, SA, SA_certainty, outputStartFrame, outputEndFrame, Object, ObjectID, ObjectPosition, new float[]{RightHandDistance, LeftHandDistance}); // TODO: Fix: 2 objects needed?
                    objectEvents++;
                    previousSubActivity = SA;
                }
            }
            //*********** Last Activity
            // STEP 0 -PRE-FILTER: RATIO OF SA-OBJECTS OCCURRENCES
            System.out.print("\n- Activity " + activ + ". Ratio pre-filter passed by: ");
            detectedActivities.clear();
            ActivitiesCertaintiesPairs preRatioActivitiesCertaintiesPairs = filterActivitiesContainingSubActivityAndObjectConstraints();
            detectedActivities = preRatioActivitiesCertaintiesPairs.getActivities();
            // STEP 3 - FINAL FILTER: DEGREE OF CERTAINTY ASSESSMENT
            activCertainties.clear();
            candidateActivities.clear();
            System.out.print("\n  ******** Activity "+activ+". Certainties of candidate activities: ");//All constraints passed by: ");
            for (int i=0; i<detectedActivities1.size(); i++){  // OBJECT POSITIONS
                activCertainties.add(round((float) getCertaintyOfActivityHappeningFDL(detectedActivities.get(i)), 2)); // BIGGEST RUNNING TIME OVERHEAD: deleting KB, inserting axioms and querying.
                candidateActivities.add(detectedActivities.get(i));
                System.out.print(detectedActivities.get(i)+ "("+activCertainties.get(activCertainties.size()-1) + ") ");
            }
            // CLASSIFICATION RESULTS
            if (detectedActivities1.size()< 1){
                classification[activities.get(activ)][activities.size()]+= 1; // Filling the null (last) column
                System.out.println( "   -"+nActivities+ " "+activityID+" -> Detected as NULL ____________________________________ ");
            }
            else{ // OBJECT POSITIONS / HIGHEST CERTAINTY    //FILTRAR LA DE MAYOR UMBRAL- ASIGNAR UMBRAL A PARTIR DEL NUMERO De PATRONES CUMPLIDOS/INTERACCIONES CON OBJETOS Y RESTRICCIONES DE OBJECT POS.
                String detectedActivity = getNaiveActivityWithHighestCertaintyOverThreshold(activCertainties, candidateActivities, activDetectionThreshold, secondRatioActivitiesCertaintiesPairs, preRatioActivitiesCertaintiesPairs); // Gives "" for cereal!
                if(!detectedActivity.equals("")){ // if activity involves SubActivities of stackable, check their objects positions
                    classification[activities.get(activ)][activities.get(detectedActivity)]+= 1;
                    if(detectedActivity.equals(activ)){
                        sumRightGuesses++;
                        System.out.println("  ------------------------------------------ BINGO: " + activ );
                    }
                    else
                        System.out.println("   -"+nActivities+ " "+activityID+" -> Detected as: "+ detectedActivity);
                }
                else
                    classification[activities.get(activ)][activities.size()]+= 1; // NULL
            }
            nActivities++;
            objectEvents++;
            // **********

            System.out.println("Confusion Matrix (RealActivities X ClassifiedActivities)");
            printMatrix(classification);
            float [][] classificationNormalized = new float[activities.size()][activities.size()+1];
            for (int i= 0; i<classification.length; i++){
                float activitySum = 0;
                for(int j=0; j<classification[i].length; ++j)
                    activitySum += (float)classification[i][j];
                for(int j=0; j<classification[i].length; ++j)
                    classificationNormalized[i][j] = round(classification[i][j]/activitySum, 3);
            }
            System.out.println("Confusion Matrix normalized (%)(RealActivities X ClassifiedActivities)");
            printMatrix(classificationNormalized);

            writeConfusionMatrixToResultsFile(resultsFile, classificationNormalized);

            //Close the input stream
            in.close();
            ////////////////////////////////////////////////////////////// COMPUTE STATS
            int fn, fp, tp, tn; // to compute accuracy, not precision            //See more at: http://blog.techmens.com/precision-and-recall-how/#sthash.nSEoKJWK.dpuf
            float precision;
            float recall;
            float hitRate;
            for (int i= 0; i<classification.length; i++){
                fn = 0; // to compute accuracy, not precision
                fp = 0;
                tp = 0;
                tn = 0;
                for(int j=0; j<classification[i].length; ++j){
                    if(classification[i][j] > 0)
                        if (i==j)
                            tp += classification[i][j];
                        else
                        if (j == activities.size()) // null column WRONG!!!
                            fn += classification[i][j];
                        else
                            fp += classification[i][j];
                }
                if(tp+fp ==0)
                    precision = 0;
                else
                    precision = (float)tp/(tp+fp);//round((float)tp/(tp+fp), 3);
                System.out.println("PRECISION for activity "+(i+1)+": "+precision);
                if (tp+fn ==0)
                    recall =0;
                else
                    recall = (float)tp/(tp+fn);// round((float)tp/(tp+fn), 3);
                statisticsTP_TN_FP_FN[i][0] = tp;
                statisticsTP_TN_FP_FN[i][1] = tn;
                statisticsTP_TN_FP_FN[i][2] = fp;
                statisticsTP_TN_FP_FN[i][3] = fn;
                statisticsTP_TN_FP_FN[i][4] = precision;
                statisticsTP_TN_FP_FN[i][5] = recall;
            }
            System.out.println("X: Activities");
            System.out.println("Y: TP | TN | FP | FN | Precision | Recall");

            printMatrix(statisticsTP_TN_FP_FN);
            float sumPrec =0f;
            float sumRecall = 0f;
            for(int i=0; i<statisticsTP_TN_FP_FN.length; ++i){
                sumPrec += statisticsTP_TN_FP_FN[i][4];
                sumRecall += statisticsTP_TN_FP_FN[i][5];
            }

            precision = round(sumPrec/activities.size(), 3);
            recall = round(sumRecall/activities.size(), 3);
            hitRate = round(((float)sumRightGuesses)/nActivities, 3);
            //float accuracy = round((((float)(tp + tn))/(tp + tn + fp + fn)), 2);
            System.out.println("---> Avg precision and recall: "+precision+ " "+recall + " for total: "+ nSubActivitiesDetected+" SubActivities simulated, "+(nActivities)+" Activities and "+objectEvents+" objectEvents. KBmirrorSubAct: "+KBmirrorSubActivities.size());
            System.out.println("---> Accuracy: "+hitRate);//accuracy+" (hitRate "+hitRate+")");
            results[0] = nActivities; // The number of activities simulated according to the Activity ID in the input test file
            results[1] = precision;
            results[2] = recall;
            results[3] = hitRate; // = same as accuracy

            writeToFile(resultsFilePrecRecallAccuracy, Float.toString(precision).concat(" , ").concat(Float.toString(recall)).concat(" , ").concat(Float.toString(hitRate)).concat("\n"));

        }catch (Exception e){//Catch exception if any
            System.err.println("Error reading dataset file in naiveSubActivityEventSimulator: " + e.getMessage());
        }
        return results;
    }

    public static float[] SubActivityEventSimulator(String file, float activDetectionThreshold){
        // E.g.: 126141638,"cereal","988","moving","50","105","53","2","box","516.5936279296875","661.789306640625","364.231 305.74 114.842"
        // ActID, ActName, SubActID, SubActName, SubActStartFrame, SubActEndFrame, FrameNr, ObjectID, ObjectName, DistanceRight, DistanceLeft, Pos3D
        // Idact.frame.objId.distLeft.distRight.objName.pos3D Distance is in cm. Touching happens when smaller than max. ~52cm.
        // Input event is considered all the time; however, the object is annotated only when the distance to any of the objects is closer than 49cm. Otherwise it is "".
        int nActivities = 0;
        int objectEvents = 0;
        nSubActivitiesDetected = 0;
        realFrameClock = 0;
        int sumRightGuesses = 0;
        float[] results = new float[4]; // N. of activities simulated, precision and recall of the simulation experiment
        ActivitiesCertaintiesPairs secondRatioActivitiesCertaintiesPairs = new ActivitiesCertaintiesPairs(new ArrayList<String>(), new ArrayList<Float>());
        // format example: 126141638,"cereal","988","moving","50","105"
        try{
            FileInputStream fstream = new FileInputStream(file);       //"dataset.txt"
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            //Confusion matrix for high-level activity labeling: Initialization
            int [][] classification = new int[activities.size()][activities.size()+1];
            for (int i=0; i< classification.length; ++i)
                for (int j =0; j<classification[i].length; j++)
                    classification[i][j] = 0;
            float [][] statisticsTP_TN_FP_FN = new float[activities.size()][6];
            for (int i=0; i< statisticsTP_TN_FP_FN.length; ++i)
                for (int j =0; j<statisticsTP_TN_FP_FN[i].length; j++)
                    statisticsTP_TN_FP_FN[i][j] = 0;

            String activ = ""; // FOR SIMULATION PURPOSES
            String activityID = "";
            ArrayList<String> detectedActivities = new ArrayList<String>();
            ArrayList<String> detectedActivities1 = new ArrayList<String>();
            int previousEndFrame = 0;
            int currentStartFrame = -1;
            int currentEndFrame = -1;
            int outputStartFrame = -1;
            int outputEndFrame = -1;
            String previousSubActivity = "";
            // FOR FILE CROSS VALIDATION PURPOSES
            //5,"126141638","cereal","988","moving","50","105","50","1","bowl","136.4384307861328","147.10508728027344","-20.406 684.239 331.827"
            int UserID, StartFrame, EndFrame, CurrentFrame;
            String SA = "";
            String Activity = "";
            String Object = "";
            String ActivityID, SA_ID, ObjectID;
            float LeftHandDistance = 0;
            float RightHandDistance = 0;
            float ObjectPosX = 0;
            float ObjectPosY = 0;
            float ObjectPosZ = 0;
            float [] ObjectPosition = new float[3];
            Double SA_certainty = 0.0;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null){ //for each sub-activity register
                String[] register = strLine.split(",");
                for (int i=0; i<register.length; ++i)
                    register[i] = removeQuotes(register[i]);
                if(file.contains("PerfectLabels")){ // WITH PERFECT DATASET LABELS FOR EACH SUB-ACTIVITY DETECTED
                    UserID= Integer.parseInt(register[0]);
                    ActivityID = register[1];
                    Activity = register[2];
                    SA_ID = register[3];
                    SA = register[4];
                    StartFrame = Integer.parseInt(register[5]);
                    EndFrame = Integer.parseInt(register[6]);
                    CurrentFrame = Integer.parseInt(register[7]);
                    ObjectID = register[8];
                    Object = register[9];
                    RightHandDistance = Math.abs(round(Float.parseFloat(register[10]), 2));
                    LeftHandDistance = Math.abs(round(Float.parseFloat(register[11]), 2));
                    String [] positions = register[12].split(" ");
                    ObjectPosX = round(Float.parseFloat(positions[0]),2);
                    ObjectPosY = round(Float.parseFloat(positions[1]), 2);
                    ObjectPosZ = round(Float.parseFloat(positions[2]), 2);
                    ObjectPosition = new float[]{ObjectPosX, ObjectPosY, ObjectPosZ};
                    SA_certainty = defaultDegree;
                }
                else{ // WITH ERROR PROPAGATION FROM SUB-ACTIVITIES TRACKED AND DETECTED BY OLMO
                    //fold,"train_test_type","id","name","subact_id_real","name","subact_name_detected","distance_grade","start_frame","end_frame","frame","object_id","object_name","distance_left","distance_right","position"
                    //1,"1","126141638","cereal","988","moving","cleaning","0.17738458476960717","50","105","1","1","bowl","147.10508728027344","136.4384307861328","-20.406 684.239 331.827"
                    ActivityID = register[2];
                    Activity = register[3];
                    SA_ID = register[4];
                    SA = register[6];
                    StartFrame = Integer.parseInt(register[8]);
                    EndFrame = Integer.parseInt(register[9]);
                    CurrentFrame = Integer.parseInt(register[10]);
                    ObjectID = register[11];
                    Object = register[12];
                    RightHandDistance = Math.abs(round(Float.parseFloat(register[13]), 2));
                    LeftHandDistance = Math.abs(round(Float.parseFloat(register[14]), 2));
                    String [] positions = register[15].split(" ");
                    ObjectPosX = round(Float.parseFloat(positions[0]), 2);
                    ObjectPosY = round(Float.parseFloat(positions[1]), 2);
                    ObjectPosZ = round(Float.parseFloat(positions[2]), 2);
                    ObjectPosition = new float[]{ObjectPosX, ObjectPosY, ObjectPosZ};
                    SA_certainty = round(Double.parseDouble(register[7]), 2);
                }

                if (Activity.equals("eating")) Activity = "eatingMeal";
                if (Activity.equals("cleaning")) Activity = "cleaningObjects";
                if (Activity.equals("placing")) Activity = "arrangingObjects";
                if (Activity.equals("microwave")) Activity = "microwaving";
                if (Object.equals("NULL")) Object = NULL_OBJECT;
                if (!activityID.equals("") ){
                    if(!previousSubActivity.equals(SA))
                        nSubActivitiesDetected++;
                    //System.out.println("NSubActivities "+nSubActivities+". Size of KBmirrorSubAct "+KBmirrorSubActivities.size());
                    if (!ActivityID.equals(activityID)){ // NEW ACTIVITY BEING SIMULATED       // TODO: check if more than one activity is detected with same possibility.
                        // Start counting AVG ACTIVITY RECOGNITION TIME:
                        Date activRecogStartTime = new Date();

                        // STEP 0 -PRE-FILTER: RATIO OF SA-OBJECTS OCCURRENCES
                        System.out.print("\n- Activity " + activ + ". Ratio pre-filter passed by: ");
                        detectedActivities.clear();
                        ActivitiesCertaintiesPairs preRatioActivitiesCertaintiesPairs = filterActivitiesContainingSubActivityAndObjectConstraints();
                        detectedActivities = preRatioActivitiesCertaintiesPairs.getActivities();
                        //for (int i=0; i<detectedActivities.size(); i++){  // RATIO PRE-FILTER
                          //  System.out.print(detectedActivities.get(i)+ " - ");
                        //}
                        // STEP 1 - ORDER OF SA AND OBJECT PAIRS
                        System.out.print("\n- Activity "+activ+". Order-Object constraints passed by: ");
                        detectedActivities1.clear();
                        detectedActivities1 = filterActivitiesFulfillingOrderAndObjectConstraints(detectedActivities);  // TODO add check if not empty
                        if(detectedActivities1.size()<1){ // SECOND OPPORTUNITY OF RECOGNITION WITH LOWEST THRESHOLD
                            System.out.print("\n- Activity "+activ+". Ratio2 of Sub-Sequences of SA-objects pairs passed by: ");
                            secondRatioActivitiesCertaintiesPairs = filterActivitiesFulfillingOrderAndObjectSubSequenceConstraints(detectedActivities);
                            detectedActivities1 = secondRatioActivitiesCertaintiesPairs.getActivities();
                        }
                        if(detectedActivities1.size()<1)
                            detectedActivities1 = (ArrayList<String>)detectedActivities.clone();
                        /*/ STEP 2 - EXTRA CONSTRAINTS FILTER
                        detectedActivities2.clear();
                        detectedActivities2 = filterActivitiesFulfillingExtraConstraints(detectedActivities1);
                        System.out.print("\n- Activity "+activ+". Extra constraints passed by: ");
                        for (int i=0; i<detectedActivities2.size(); i++)  // OBJECT CONSTRAINTS
                            System.out.print(detectedActivities2.get(i)+ " - ");*/

                        // STEP 3 - FINAL FILTER: DEGREE OF CERTAINTY ASSESSMENT
                        activCertainties.clear();
                        candidateActivities.clear();
                        System.out.print("\n- Activity "+activ+". Certainties of candidate activities: ");//All constraints passed by: ");
                        for (int i=0; i<detectedActivities1.size(); i++){  // OBJECT POSITIONS
                            activCertainties.add(getCertaintyOfActivityHappeningFDL(detectedActivities1.get(i))); // BIGGEST RUNNING TIME OVERHEAD: deleting KB, inserting axioms and querying.
                            candidateActivities.add(detectedActivities1.get(i));
                            System.out.print(candidateActivities.get(i)+ "("+activCertainties.get(i) + ") ");
                        }
                        // CLASSIFICATION RESULTS
                        if (candidateActivities.size()< 1){
                            classification[activities.get(activ)][activities.size()]+= 1; // Filling the null (last) column
                            System.out.println( "   -"+nActivities+ " "+activityID+" -> Detected as NULL ____________________________________ ");
                        }
                        else{ // OBJECT POSITIONS / HIGHEST CERTAINTY    //FILTRAR LA DE MAYOR UMBRAL- ASIGNAR UMBRAL A PARTIR DEL NUMERO De PATRONES CUMPLIDOS/INTERACCIONES CON OBJETOS Y RESTRICCIONES DE OBJECT POS.
                            String detectedActivity = getActivityWithHighestCertaintyOverThresholdAndObjPosConstraints(activCertainties, candidateActivities, activDetectionThreshold, secondRatioActivitiesCertaintiesPairs, preRatioActivitiesCertaintiesPairs); // Gives "" for cereal!
                            if(!detectedActivity.equals("")){ // if activity involves SubActivities of stackable, check their objects positions
                                classification[activities.get(activ)][activities.get(detectedActivity)]+= 1;
                                if(detectedActivity.equals(activ)){
                                    sumRightGuesses++;
                                    System.out.println("  ------------------------------------------ BINGO: " + activ );
                                }
                                else
                                    System.out.println("   -"+nActivities+ " "+activityID+" -> Detected as: "+ detectedActivity);
                            }
                            else{
                                System.out.println( "   -"+nActivities+ " "+activityID+" -> Detected as NULL ____________________________________ ");
                                classification[activities.get(activ)][activities.size()]+= 1; // NULL
                            }
                        }
                        // Compute duration of query + recognition time getTime() returns milliseconds
                        Date endTime = new Date(); // pre-set to 1h (max of the avg times of each activity type)
                        //System.out.print("QueryTime: "+(endTime.getTime() - activRecogStartTime.getTime())+" ");
                        writeToFile(recognitionDurationsFile+activ+".txt", Float.toString(endTime.getTime() - activRecogStartTime.getTime()).concat("\n"));

                        nActivities++;
                        objectEvents++;
                        activityID = ActivityID;
                        activ = Activity;

                        currentStartFrame = StartFrame;
                        currentEndFrame = EndFrame;
                        realFrameClock += previousEndFrame;
                        outputStartFrame = realFrameClock + currentStartFrame;
                        outputEndFrame = realFrameClock + currentEndFrame;
                        previousEndFrame = currentEndFrame;
                        //if(SA_certainty> minSubActDetectionThreshold)
                        onNewEvent(SA_ID, SA, SA_certainty, outputStartFrame, outputEndFrame, Object, ObjectID, ObjectPosition, new float[]{RightHandDistance, LeftHandDistance}); // TODO: Fix: 2 objects needed?
                        previousSubActivity = SA; //System.out.println("Returned "+predictedActivities.size()+" predicted activities");

                    }
                    else{ // Same activity, already started, running
                        activ = Activity;
                        activityID = ActivityID;
                        currentStartFrame = StartFrame;
                        currentEndFrame = EndFrame;
                        outputStartFrame = realFrameClock + currentStartFrame;
                        outputEndFrame = realFrameClock + currentEndFrame;
                        previousEndFrame = currentEndFrame;
                        //if(SA_certainty> minSubActDetectionThreshold)
                        onNewEvent(SA_ID, SA, SA_certainty, outputStartFrame, outputEndFrame, Object, ObjectID, ObjectPosition, new float[]{RightHandDistance, LeftHandDistance}); // TODO: Fix: 2 objects needed?
                        objectEvents++;
                        previousSubActivity = SA;
                    }
                }
                else { //First, initialization step (First SubActivity)
                    activityID = ActivityID;
                    activ = Activity;
                    outputStartFrame = (realFrameClock+ (StartFrame-realFrameClock));
                    outputEndFrame = (outputStartFrame+(EndFrame - StartFrame));
                    previousEndFrame = outputEndFrame;
                    //if(SA_certainty> minSubActDetectionThreshold)
                    onNewEvent(SA_ID, SA, SA_certainty, outputStartFrame, outputEndFrame, Object, ObjectID, ObjectPosition, new float[]{RightHandDistance, LeftHandDistance}); // TODO: Fix: 2 objects needed?
                    objectEvents++;
                    previousSubActivity = SA;
                }
            }
            //*********** Last Activity
            // STEP 0 -PRE-FILTER: RATIO OF SA-OBJECTS OCCURRENCES
            System.out.print("\n- Activity " + activ + ". Ratio pre-filter passed by: ");
            detectedActivities.clear();
            ActivitiesCertaintiesPairs preRatioActivitiesCertaintiesPairs = filterActivitiesContainingSubActivityAndObjectConstraints();
            detectedActivities = preRatioActivitiesCertaintiesPairs.getActivities();
            // STEP 1 - ORDER OF SA AND OBJECT PAIRS
            detectedActivities1.clear();
            detectedActivities1 = filterActivitiesFulfillingOrderAndObjectConstraints(detectedActivities);  // TODO add check if not empty
            System.out.print("\n- Activity "+activ+". Order-Object constraints passed by: ");
            for (int i=0; i<detectedActivities1.size(); i++)  // OBJECT CONSTRAINTS
                System.out.print(detectedActivities1.get(i)+ " - ");

            if(detectedActivities1.size()<1){ // SECOND OPPORTUNITY OF RECOGNITION WITH LOWEST THRESHOLD
                System.out.print("\n- Activity "+activ+". Ratio2 of Sub-Sequences of SA-objects pairs passed by: ");
                secondRatioActivitiesCertaintiesPairs = filterActivitiesFulfillingOrderAndObjectSubSequenceConstraints(detectedActivities);
                detectedActivities1 = secondRatioActivitiesCertaintiesPairs.getActivities();
            }
            if(detectedActivities1.size()<1)
                detectedActivities1 = detectedActivities;
            /*/ STEP 2 - EXTRA CONSTRAINTS FILTER
            detectedActivities2.clear();
            detectedActivities2 = filterActivitiesFulfillingExtraConstraints(detectedActivities1);
            System.out.print("\n- Activity "+activ+". Extra constraints passed by: ");
            for (int i=0; i<detectedActivities2.size(); i++)  // OBJECT CONSTRAINTS
                System.out.print(detectedActivities2.get(i)+ " - ");*/

            // STEP 3 - FINAL FILTER: DEGREE OF CERTAINTY ASSESSMENT
            activCertainties.clear();
            candidateActivities.clear();
            System.out.print("\n  ******** Activity "+activ+". Certainties of candidate activities: ");//All constraints passed by: ");
            for (int i=0; i<detectedActivities1.size(); i++){  // OBJECT POSITIONS
                activCertainties.add(round((float) getCertaintyOfActivityHappeningFDL(detectedActivities1.get(i)), 2)); // BIGGEST RUNNING TIME OVERHEAD: deleting KB, inserting axioms and querying.
                candidateActivities.add(detectedActivities1.get(i));
                System.out.print(detectedActivities1.get(i)+ "("+activCertainties.get(activCertainties.size()-1) + ") ");
            }
            // CLASSIFICATION RESULTS
            if (detectedActivities1.size()< 1){
                classification[activities.get(activ)][activities.size()]+= 1; // Filling the null (last) column
                System.out.println( "   -"+nActivities+ " "+activityID+" -> Detected as NULL ____________________________________ ");
            }
            else{ // OBJECT POSITIONS / HIGHEST CERTAINTY    //FILTRAR LA DE MAYOR UMBRAL- ASIGNAR UMBRAL A PARTIR DEL NUMERO De PATRONES CUMPLIDOS/INTERACCIONES CON OBJETOS Y RESTRICCIONES DE OBJECT POS.
                String detectedActivity = getActivityWithHighestCertaintyOverThresholdAndObjPosConstraints(activCertainties, candidateActivities, activDetectionThreshold, secondRatioActivitiesCertaintiesPairs, preRatioActivitiesCertaintiesPairs); // Gives "" for cereal!
                if(!detectedActivity.equals("")){ // if activity involves SubActivities of stackable, check their objects positions
                    classification[activities.get(activ)][activities.get(detectedActivity)]+= 1;
                    if(detectedActivity.equals(activ)){
                        sumRightGuesses++;
                        System.out.println("  ------------------------------------------ BINGO: " + activ );
                    }
                    else
                        System.out.println("   -"+nActivities+ " "+activityID+" -> Detected as: "+ detectedActivity);
                }
                else
                    classification[activities.get(activ)][activities.size()]+= 1; // NULL
            }
            nActivities++;
            objectEvents++;
            // **********

            System.out.println("Confusion Matrix (RealActivities X ClassifiedActivities)");
            printMatrix(classification);
            float [][] classificationNormalized = new float[activities.size()][activities.size()+1];
            for (int i= 0; i<classification.length; i++){
                float activitySum = 0;
                for(int j=0; j<classification[i].length; ++j)
                    activitySum += (float)classification[i][j];
                for(int j=0; j<classification[i].length; ++j)
                    classificationNormalized[i][j] = round(classification[i][j]/activitySum, 3);
            }
            System.out.println("Confusion Matrix normalized (%)(RealActivities X ClassifiedActivities)");
            printMatrix(classificationNormalized);

            writeConfusionMatrixToResultsFile(resultsFile, classificationNormalized);

            //Close the input stream
            in.close();
            ////////////////////////////////////////////////////////////// COMPUTE STATS
            int fn, fp, tp, tn; // to compute accuracy, not precision            //See more at: http://blog.techmens.com/precision-and-recall-how/#sthash.nSEoKJWK.dpuf
            float precision;
            float recall;
            float hitRate;
            for (int i= 0; i<classification.length; i++){
                fn = 0; // to compute accuracy, not precision
                fp = 0;
                tp = 0;
                tn = 0;
                for(int j=0; j<classification[i].length; ++j){
                    if(classification[i][j] > 0)
                        if (i==j)
                            tp += classification[i][j];
                        else
                            if (j == activities.size()) // null column WRONG!!!
                                fn += classification[i][j];
                            else
                                fp += classification[i][j];
                }
                if(tp+fp ==0)
                    precision = 0;
                else
                    precision = (float)tp/(tp+fp);//round((float)tp/(tp+fp), 3);
                System.out.println("PRECISION for activity "+(i+1)+": "+precision);
                if (tp+fn ==0)
                    recall =0;
                else
                    recall = (float)tp/(tp+fn);// round((float)tp/(tp+fn), 3);
                statisticsTP_TN_FP_FN[i][0] = tp;
                statisticsTP_TN_FP_FN[i][1] = tn;
                statisticsTP_TN_FP_FN[i][2] = fp;
                statisticsTP_TN_FP_FN[i][3] = fn;
                statisticsTP_TN_FP_FN[i][4] = precision;
                statisticsTP_TN_FP_FN[i][5] = recall;
            }
            System.out.println("X: Activities");
            System.out.println("Y: TP | TN | FP | FN | Precision | Recall");

            printMatrix(statisticsTP_TN_FP_FN);
            float sumPrec =0f;
            float sumRecall = 0f;
            for(int i=0; i<statisticsTP_TN_FP_FN.length; ++i){
                sumPrec += statisticsTP_TN_FP_FN[i][4];
                sumRecall += statisticsTP_TN_FP_FN[i][5];
            }

            precision = round(sumPrec/activities.size(), 3);
            recall = round(sumRecall/activities.size(), 3);
            hitRate = round(((float)sumRightGuesses)/nActivities, 3);
            //float accuracy = round((((float)(tp + tn))/(tp + tn + fp + fn)), 2);
            System.out.println("---> Avg precision and recall: "+precision+ " "+recall + " for total: "+ nSubActivitiesDetected+" SubActivities simulated, "+(nActivities)+" Activities and "+objectEvents+" objectEvents. KBmirrorSubAct: "+KBmirrorSubActivities.size());
            System.out.println("---> Accuracy: "+hitRate);//accuracy+" (hitRate "+hitRate+")");
            results[0] = nActivities; // The number of activities simulated according to the Activity ID in the input test file
            results[1] = precision;
            results[2] = recall;
            results[3] = hitRate; // = same as accuracy

            writeToFile(resultsFilePrecRecallAccuracy, Float.toString(precision).concat(" , ").concat(Float.toString(recall)).concat(" , ").concat(Float.toString(hitRate)).concat("\n"));

        }catch (Exception e){//Catch exception if any
            System.err.println("Error reading dataset file in SubActivityEventSimulator: " + e.getMessage());
        }
        return results;
    }

    protected String getTimestamp (String activityIndividualName){
        String date = " ";
        return date;
    }

    protected static void initializeKB(String userName) throws InconsistentOntologyException{
        try {
            // Retrieving individual:
            userIndividual = kb.getIndividual(userName);
            closeObjectInteraction = new LeftConcreteConcept("closeObjectInteraction", 0.0, 100000, closeObjectInteractionA, closeObjectInteractionB); // max distance supported?
            // Load options for the reasoner, using file "CONFIG"
            ConfigReader.loadParameters("CONFIG", new String[0]);

            // Append the rules once
            /*BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter pw = new PrintWriter(new FileWriter(KBFile, true));
            String rules = LearnWeightsAndCreateRules(trainingFile);

            try {
                pw.println(rules);
            }
            catch (Exception e){
                System.err.println("Error writing rules to file: " + e.getMessage());//oh noes!
            }
            finally{
                pw.close();
            }*/
            // Option 2. Read knowledge base and queries from file "fileName.txt"
            Parser parser = new Parser(new FileInputStream(KBFileWithoutRules));   //Kinect_v2.txt"));
            //this.parser = parser;
            parser.Start();
            kb = parser.getKB().clone();
            // After having created KB and queries, start logical inference
            kb.solveKB();
            //activitiesDetectionThresholds = new ArrayList<Float>(Collections.nCopies(activities.size(), activityDetectionThreshold));
            /*float maxDur1 = activitiesDurationsMean_Stdev.get(activities.get(STACKING)).get(2);
            float maxDur2 = activitiesDurationsMean_Stdev.get(activities.get(UNSTACKING)).get(2);
            if (maxDur1> maxDur2)
                maxDurationStackUnstacking = STACKING;
            else maxDurationStackUnstacking = UNSTACKING;*/
        }
        catch (Exception e){//Catch exception if any
            System.err.println("Error reading dataset file in initializeKB: " + e.getMessage());
        }
    }

    protected static void setParserWithFileAndEmptyKB(String file){
        try{
            // Load options for the reasoner, using file "CONFIG"
            ConfigReader.loadParameters("CONFIG", new String[0]);

            //Parser p = new Parser(new FileInputStream(file));
            //parser = p;
            parser = new Parser(new FileInputStream(file));
            parser.Start();

            kb = parser.getKB().clone();
            // After having created KB and queries, start logical inference
            kb.solveKB();


            startQuery = new KbSatisfiableQuery();
            startQuerySolution = startQuery.solve(kb);
            if (startQuerySolution.isConsistentKB())  {
                System.out.println("KB is consistent");
                //System.out.println(q1.toString() + result1.getSolution());
            }
            else{
                System.out.println("KB is inconsistent!");
                System.exit(0);
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in setParserWithFile: " + e.getMessage());
        }
    }
    protected static void emptyAndRestartKB(){
        try{
            //kb = null;           //kb.dispose();
            KBmirrorActivStartFrames.clear();
            KBmirrorActivEndFrames.clear();
            KBmirrorActivID.clear();
            KBmirrorActivConcept.clear();
            detectedCriticalActivities.clear();

            // Read knowledge base and queries from file "fileName.txt"
            //kb = new KnowledgeBase();
            //
           /*if(parser == null){
               parser = new Parser(new FileInputStream(parseFileBeingCrossValidated));
               parser.Start();
               //parser.clearKB();
           }/*else
                parser.ReInit(new FileInputStream(KBFile));
            //else
              //  parser.clearKB();*/

            kb = parser.getKB().clone(); // Very Important! otherwise KB is not cleaned and it goes super slow with time
            kb.solveKB(); // Important also!


            // Solve a query q
            startQuery = new KbSatisfiableQuery();
            startQuerySolution = startQuery.solve(kb);
            if (!startQuerySolution.isConsistentKB())  {
                //System.out.println("KB is consistent");
            //}
            //else{
                System.out.println("KB is inconsistent!");
                System.exit(0);
            }
            // Queries were also part of the file "fileName.txt"
            //ArrayList <Query> queries = parser.getQueries();
            // After having created KB and queries, start logical inference
            //kb.solveKB();

        }catch (Exception e){//Catch exception if any
            System.err.println("Error reading fuzzyDL KB file: " + e.getMessage());
        }
    }

    public static void LearnMeanAndStdDevDurationsAndSetPrecedesThreshold(String file){
        try{
            FileInputStream fstream = new FileInputStream(file);       //"dataset.txt"
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Durations in number of frames
            ArrayList<Integer> durations = new ArrayList<Integer>();
            ArrayList<ArrayList<Integer>> activitiesDurations = new ArrayList<ArrayList<Integer>>();
            ArrayList<ArrayList<Integer>> activitiesDurationsInSubActs = new ArrayList<ArrayList<Integer>>();
            //Initialization
            for (int i=0; i<activities.size(); i++){
                activitiesDurations.add(new ArrayList<Integer>());
                activitiesDurationsInSubActs.add(new ArrayList<Integer>());
                activitiesDurationsMean_Stdev.add(new ArrayList<Float>());
            }
            String activityID = "";
            String activity = "";
            int startTime = 0;
            int endTime = 0;
            int totalDurationNulls = 0; // We calculate avg duration of activity null to set it as threshold for precedes method (var precedesThresholdInFrames)
            int nNulls = 0;
            int nSubAct = 0;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null){ //for each sub-activity register
                // Print the content on the console
                String[] register = strLine.split(",");
                // Removing "" quotes from register data
                for (int i=0; i<register.length; ++i){
                    register[i] = removeQuotes(register[i]);
                }
                if (register[1].equals("eating")) register[1] = "eatingMeal";
                if (register[1].equals("cleaning")) register[1] = "cleaningObjects";
                if (register[1].equals("placing")) register[1] = "arrangingObjects";
                if (register[1].equals("microwave")) register[1] = "microwaving";
                // We add the durations to their activity
                if (!activityID.equals("")){
                    if(register[3].equals("null")){ // Does not work with NULL constant
                        totalDurationNulls += (Integer.parseInt(register[5])-Integer.parseInt(register[4]));
                        nNulls ++;
                        //nSubAct++;
                    }
                    if (!register[0].equals(activityID)){
                        //System.out.println("Inserting into :" + activities.get(activity));
                        activitiesDurations.get(activities.get(activity)).add(endTime - startTime);
                        activitiesDurationsInSubActs.get(activities.get(activity)).add(nSubAct);
                        nSubAct = 1;
                        activityID = register[0];
                        startTime = Integer.parseInt(register[4]);
                        activity = register[1];
                    }
                    else{
                        endTime = Integer.parseInt(register[5]);
                        //activityID = register[0];
                        activity = register[1];
                        nSubAct++;
                    }
                }
                else {
                    activityID = register[0];
                    activity = register[1];
                    startTime = Integer.parseInt(register[4]);
                    nSubAct++;
                }
            }
            precedesThresholdInFrames = totalDurationNulls / nNulls;

            //Computing avg and std deviations of durations in Nr of SubAct. and in Nr of frames
            //DecimalFormat df = new DecimalFormat("##");
            //df.setRoundingMode(RoundingMode.UP);   // Ex.:// Good: truncates up and zero decimals (1 would be #.#):    System.out.println(df.format(12.49688f));
            for(int u =0; u<activitiesDurationsInSubActs.size(); ++u){
                int maxDurationInNrOfSubAct = getMax(activitiesDurationsInSubActs.get(u));
                activitiesDurationsInNrOfSubActivities.add(maxDurationInNrOfSubAct);
            }
            for(int i = 0; i<activitiesDurations.size(); i++){
                // float sum = 0f;
                // for (int j=0; j<activitiesDurations.get(i).size(); ++j)
                //   sum += activitiesDurations.get(i).get(j);
                //activitiesFTW.add(new Float(sum/(activitiesDurations.get(i).size())));
                //DecimalFormat f = new DecimalFormat(activitiesDurations.get(i));
                if(activitiesDurations.get(i).size()>0){
                    float mean = getMean(activitiesDurations.get(i));
                    float stdev = getStdDev(activitiesDurations.get(i));
                    float maxDuration = getMax(activitiesDurations.get(i));
                    //System.out.println("mean and std: "+mean +" "+stdev);
                    BigDecimal bd = new BigDecimal(mean);
                    bd=bd.setScale(2,BigDecimal.ROUND_HALF_EVEN);
                    BigDecimal bd2 = new BigDecimal(stdev);
                    bd2=bd2.setScale(2,BigDecimal.ROUND_HALF_EVEN);
                    BigDecimal bd3 = new BigDecimal(maxDuration);
                    bd3=bd3.setScale(2,BigDecimal.ROUND_HALF_EVEN);
                    //System.out.println("bigdecimal: "+bd.floatValue());
                    activitiesDurationsMean_Stdev.get(i).add(bd.floatValue()); // FIRST ELEMENT IS THE MEAN
                    activitiesDurationsMean_Stdev.get(i).add(bd2.floatValue()); // SECOND ELEMENT IS THE STD DEV
                    activitiesDurationsMean_Stdev.get(i).add(bd3.floatValue()); // THIRD ELEMENT IS THE MAX
                }
            }

            for(int i=0; i< activitiesDurationsInNrOfSubActivities.size(); ++i)
                System.out.println("ActivAvgDurationInNrOfSubActs: "+ activitiesDurationsInNrOfSubActivities.get(i));
            float maxAvg = 0.0f;
            float maxStdev = 0.0f;
            for (int i=0; i<activitiesDurationsMean_Stdev.size(); ++i)
                if ( activitiesDurationsMean_Stdev.get(i).size()>0 && activitiesDurationsMean_Stdev.get(i).get(0) > maxAvg){
                    maxAvg = activitiesDurationsMean_Stdev.get(i).get(0);
                    maxStdev = activitiesDurationsMean_Stdev.get(i).get(1);
                }
            KBLifetimeInFrames = (int)(maxAvg+ maxStdev)*3; // security buffer to detect Activities
            for(int i = 0; i<activitiesDurations.size(); i++){
                if(activitiesDurations.get(i).size()>0){
                    System.out.println(activitiesDurations.get(i));
                    System.out.println(activitiesDurationsMean_Stdev.get(i));
                }
            }
            System.out.println("KB lifetime in frames was set to: "+ KBLifetimeInFrames);//

            // Hierarchy used for subsumption by # of SA property
            subsumesBySAAvgLengthCategory.put(CEREAL, activitiesDurationsInNrOfSubActivities.get(activities.get(CEREAL)));
            subsumesBySAAvgLengthCategory.put(MEDICINE, activitiesDurationsInNrOfSubActivities.get(activities.get(MEDICINE)));
            subsumesBySAAvgLengthCategory.put(STACKING,  activitiesDurationsInNrOfSubActivities.get(activities.get(STACKING)));
            subsumesBySAAvgLengthCategory.put(UNSTACKING,  activitiesDurationsInNrOfSubActivities.get(activities.get(UNSTACKING)));
            subsumesBySAAvgLengthCategory.put(MICROWAVING,  activitiesDurationsInNrOfSubActivities.get(activities.get(MICROWAVING)));
            subsumesBySAAvgLengthCategory.put(BENDING,  activitiesDurationsInNrOfSubActivities.get(activities.get(BENDING)));
            subsumesBySAAvgLengthCategory.put(CLEANING_OBJECTS,  activitiesDurationsInNrOfSubActivities.get(activities.get(CLEANING_OBJECTS)));
            subsumesBySAAvgLengthCategory.put(TAKEOUT,  activitiesDurationsInNrOfSubActivities.get(activities.get(TAKEOUT)));
            subsumesBySAAvgLengthCategory.put(ARRANGING_OBJECTS,  activitiesDurationsInNrOfSubActivities.get(activities.get(ARRANGING_OBJECTS)));
            subsumesBySAAvgLengthCategory.put(EATING_MEAL,  activitiesDurationsInNrOfSubActivities.get(activities.get(EATING_MEAL)));

            //Close the input stream
            in.close();
        }
        catch (Exception e){//Catch exception if any
            System.err.println("Error reading dataset file in LearnAvgMean_Stdev: " + e.getMessage());
        }
    }

    protected static void printMatrix(int[][] matrix){
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.print("\n");
        }
    }
    protected static void printMatrix(float[][] matrix){
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                //System.out.printf("%1f ", round(matrix[i][j], 2));
                System.out.print(matrix[i][j] + "  ");
            }
            System.out.print("\n");
        }
    }
    protected static void printMatrix(ArrayList<ArrayList<Float>> matrix){
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                //System.out.printf("%1f ", round(matrix[i][j], 2));
                System.out.print(matrix.get(i).get(j) + "  ");
            }
            System.out.print("\n");
        }
    }
    protected static void printMatrix(String[][] matrix){
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + "      ");
            }
            System.out.print("\n");
        }
    }

    // return phi(x) = standard Gaussian pdf
    public static double phi(double x) {
        return Math.exp(-x * x / 2) / Math.sqrt(2 * Math.PI);
    }

    // return phi(x, mu, sigma) = Gaussian pdf with mean mu and stddev sigma
    public static double phi(double x, double mu, double sigma) {
        return phi((x - mu) / sigma) / sigma;
    }

    public static float getMean(ArrayList<Integer> array)
    {
        float sum = 0f;
        for(int i=0; i<array.size(); i++)
            sum += array.get(i);
        return sum/array.size();
    }

    public static float getVariance(ArrayList<Integer> array) {
        float mean = getMean(array);
        float temp = 0;
        for(int i=0; i<array.size(); i++)
            temp += (mean-array.get(i))*(mean-array.get(i));
        return temp/array.size();
    }

    public static Integer getMax(ArrayList<Integer> array) {
        Integer max = 0;
        for(int i=0; i<array.size(); i++)
            if (array.get(i)> max)
                max = array.get(i);
        return max;
    }

    public static float getMaxFloat(ArrayList<Float> array) {
        float max = 0f;
        for(int i=0; i<array.size(); i++)
            if (array.get(i)> max)
                max = array.get(i);
        return max;
    }

    public static String getMaxDegreeActivity(ArrayList<Float> array, ArrayList<String> candidateActivities) {
        float max = -1;
        String activity = "";
        try{
            for(int i=0; i<array.size(); i++)
                if (array.get(i)> max){
                    max = array.get(i);
                    activity = candidateActivities.get(i);
                }
        }
        catch (Exception e){//Catch exception if any
            System.err.println("Error in getMaxDegreeActivity: " + e.getMessage());
        }
        return activity;
    }

    public static String[] get2MaxDegreeActivity(ArrayList<Float> certainties, ArrayList<String> candidateActivities) {
        String activity = "";
        String previousActivity = "";
        Collections.sort(certainties);
        try{
            float max = -1;
            float previousMax = -1;
            for(int i=0; i<certainties.size(); i++)
                if (certainties.get(i)> max){
                    previousMax = max;
                    previousActivity = activity;
                    max = certainties.get(i);
                    activity = candidateActivities.get(i);
                }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in get2MaxDegreeActivity: " + e.getMessage());
        }
        return new String[]{activity, previousActivity};
    }

    //Sorts according to key (Float)
    static Comparator<Dictionary<Float, String>> DictionaryComparator = new Comparator <Dictionary<Float, String>>() {
        public int compare (Dictionary<Float, String> d1, Dictionary<Float, String> d2)
        {
            Enumeration<Float> keys1 = d1.keys();
            Float cert1 = keys1.nextElement();
            Enumeration<Float> keys2 = d2.keys();
            Float cert2 = keys2.nextElement();
            // code to compare 2 dictionaries
            return cert1.compareTo(cert2);
        }
    };

    //Sorts according to value (Float)
    static Comparator<Dictionary<String, Float>> DictionaryComparatorByValue = new Comparator <Dictionary<String, Float>>() {
        public int compare (Dictionary<String, Float> d1, Dictionary<String, Float> d2)
        {
            Enumeration<String> keys1 = d1.keys();
            Float cert1 = d1.get(keys1.nextElement());
            Enumeration<String> keys2 = d2.keys();
            Float cert2 = d2.get(keys2.nextElement());
            // code to compare 2 dictionaries
            return cert1.compareTo(cert2);
        }
    };

    public static String[] sortAndGet2MaxDegreeActivity(final ArrayList<Float> certainties, final ArrayList<String> candidateActivities) {
        final ArrayList<Dictionary<Float, String>> sortedCertainties = new ArrayList<Dictionary<Float, String>>();
        String[] twoHighestCertActivities =  new String[]{"", ""};
        try{
            for(int i=0; i<certainties.size(); i++){
                Dictionary<Float, String> d = new Hashtable<Float, String>();
                d.put(certainties.get(i), candidateActivities.get(i));
                sortedCertainties.add(d);
            }
            Collections.sort(sortedCertainties, DictionaryComparator);
            if(sortedCertainties.size() == 1)
                twoHighestCertActivities[1] = sortedCertainties.get(0).get(sortedCertainties.get(0).keys().nextElement());
            else{
                twoHighestCertActivities[0] = sortedCertainties.get(sortedCertainties.size()-1).get(sortedCertainties.get(sortedCertainties.size()-1).keys().nextElement());
                twoHighestCertActivities[1] = sortedCertainties.get(sortedCertainties.size()-2).get(sortedCertainties.get(sortedCertainties.size()-2).keys().nextElement());
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in sortAndGet2MaxDegreeActivity: " + e.getMessage());
        }
        return twoHighestCertActivities;
    }

    public static ActivitiesCertaintiesPairs sortActivitiesAndCertByCertDegree(final ArrayList<Float> certainties, final ArrayList<String> candidateActivities, ArrayList<Float> secondRatioFilterRatios, ArrayList<String> secondRatioFilterActivities, ArrayList<Float> preRatioFilterRatios, ArrayList<String> preRatioFilterActivities) {
        ArrayList<Dictionary<Float, String>> sortedCertainties;
        String[] twoHighestCertActivities =  new String[]{"", ""};
        float[] twoHighestCertainties =  new float[]{0.0f, 0.0f};
        try{
            sortedCertainties = sortActivitiesByIncreasingCertainty(candidateActivities, certainties);
            if(sortedCertainties.size() == 1){
                twoHighestCertActivities[1] = sortedCertainties.get(0).get(sortedCertainties.get(0).keys().nextElement());
                twoHighestCertainties[1] = sortedCertainties.get(0).keys().nextElement();
            }
            else{
                if(cardinalOfOnes(sortedCertainties) >=2){ // We use the preRatio filter if there are 2+ with the highest certainty ( =1.0)
                    if (secondRatioFilterActivities.size()>0){
                        ArrayList<Dictionary<Float, String>> sortedSecondRatioActivCerts = sortActivitiesByIncreasingCertainty(secondRatioFilterActivities, secondRatioFilterRatios);
                        if(sortedCertainties.size() == 1){
                            twoHighestCertActivities[1] = sortedSecondRatioActivCerts.get(0).get(sortedSecondRatioActivCerts.get(0).keys().nextElement());
                            twoHighestCertainties[1] = sortedSecondRatioActivCerts.get(0).keys().nextElement();
                        }
                        else{
                            twoHighestCertActivities[0] = sortedSecondRatioActivCerts.get(sortedSecondRatioActivCerts.size()-2).get(sortedSecondRatioActivCerts.get(sortedSecondRatioActivCerts.size()-2).keys().nextElement());
                            twoHighestCertActivities[1] = sortedSecondRatioActivCerts.get(sortedSecondRatioActivCerts.size()-1).get(sortedSecondRatioActivCerts.get(sortedSecondRatioActivCerts.size()-1).keys().nextElement());
                            twoHighestCertainties[0] = sortedSecondRatioActivCerts.get(sortedSecondRatioActivCerts.size()-2).keys().nextElement();
                            twoHighestCertainties[1] = sortedSecondRatioActivCerts.get(sortedSecondRatioActivCerts.size()-1).keys().nextElement();
                        }
                    }
                    else{
                        if(preRatioFilterActivities.size()>0){
                            ArrayList<Dictionary<Float, String>> sortedPreRatioActivCerts = sortActivitiesByIncreasingCertainty(preRatioFilterActivities, preRatioFilterRatios);
                            if(sortedCertainties.size() == 1){
                                twoHighestCertActivities[1] = sortedPreRatioActivCerts.get(0).get(sortedPreRatioActivCerts.get(0).keys().nextElement());
                                twoHighestCertainties[1] = sortedPreRatioActivCerts.get(0).keys().nextElement();
                            }
                            else{
                                twoHighestCertActivities[0] = sortedPreRatioActivCerts.get(sortedPreRatioActivCerts.size()-2).get(sortedPreRatioActivCerts.get(sortedPreRatioActivCerts.size()-2).keys().nextElement());
                                twoHighestCertActivities[1] = sortedPreRatioActivCerts.get(sortedPreRatioActivCerts.size()-1).get(sortedPreRatioActivCerts.get(sortedPreRatioActivCerts.size()-1).keys().nextElement());
                                twoHighestCertainties[0] = sortedPreRatioActivCerts.get(sortedPreRatioActivCerts.size()-2).keys().nextElement();
                                twoHighestCertainties[1] = sortedPreRatioActivCerts.get(sortedPreRatioActivCerts.size()-1).keys().nextElement();
                            }
                        }
                    }
                }
                else{
                    twoHighestCertActivities[0] = sortedCertainties.get(sortedCertainties.size()-2).get(sortedCertainties.get(sortedCertainties.size()-2).keys().nextElement());
                    twoHighestCertActivities[1] = sortedCertainties.get(sortedCertainties.size()-1).get(sortedCertainties.get(sortedCertainties.size()-1).keys().nextElement());
                    twoHighestCertainties[0] = sortedCertainties.get(sortedCertainties.size()-2).keys().nextElement();
                    twoHighestCertainties[1] = sortedCertainties.get(sortedCertainties.size()-1).keys().nextElement();
                }
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in sortActivitiesAndCertByCertDegree: " + e.getMessage());
        }
        return new ActivitiesCertaintiesPairs(new ArrayList<String>(Arrays.asList(twoHighestCertActivities[0], twoHighestCertActivities[1])), new ArrayList<Float>(Arrays.asList(twoHighestCertainties[0], twoHighestCertainties[1])));
    }

    /*public static ActivitiesCertaintiesPairs sortActivitiesAndCertByCertDegreeSimple(final ArrayList<Float> certainties, final ArrayList<String> candidateActivities) {
        ArrayList<Dictionary<Float, String>> sortedCertainties;
        String[] twoHighestCertActivities =  new String[]{"", ""};
        float[] twoHighestCertainties =  new float[]{0.0f, 0.0f};
        try{
            sortedCertainties = sortActivitiesByIncreasingCertainty(candidateActivities, certainties);
            if(sortedCertainties.size() == 1){
                twoHighestCertActivities[1] = sortedCertainties.get(0).get(sortedCertainties.get(0).keys().nextElement());
                twoHighestCertainties[1] = sortedCertainties.get(0).keys().nextElement();
            }
            else{
                if(cardinalOfOnes(sortedCertainties) >=2){ // We use the preRatio filter if there are 2+ with the highest certainty ( =1.0)
                    if (secondRatioFilterActivities.size()>0){
                        ArrayList<Dictionary<Float, String>> sortedSecondRatioActivCerts = sortActivitiesByIncreasingCertainty(secondRatioFilterActivities, secondRatioFilterRatios);
                        if(sortedCertainties.size() == 1){
                            twoHighestCertActivities[1] = sortedSecondRatioActivCerts.get(0).get(sortedSecondRatioActivCerts.get(0).keys().nextElement());
                            twoHighestCertainties[1] = sortedSecondRatioActivCerts.get(0).keys().nextElement();
                        }
                        else{
                            twoHighestCertActivities[0] = sortedSecondRatioActivCerts.get(sortedSecondRatioActivCerts.size()-2).get(sortedSecondRatioActivCerts.get(sortedSecondRatioActivCerts.size()-2).keys().nextElement());
                            twoHighestCertActivities[1] = sortedSecondRatioActivCerts.get(sortedSecondRatioActivCerts.size()-1).get(sortedSecondRatioActivCerts.get(sortedSecondRatioActivCerts.size()-1).keys().nextElement());
                            twoHighestCertainties[0] = sortedSecondRatioActivCerts.get(sortedSecondRatioActivCerts.size()-2).keys().nextElement();
                            twoHighestCertainties[1] = sortedSecondRatioActivCerts.get(sortedSecondRatioActivCerts.size()-1).keys().nextElement();
                        }
                    }
                    else{
                        if(preRatioFilterActivities.size()>0){
                            ArrayList<Dictionary<Float, String>> sortedPreRatioActivCerts = sortActivitiesByIncreasingCertainty(preRatioFilterActivities, preRatioFilterRatios);
                            if(sortedCertainties.size() == 1){
                                twoHighestCertActivities[1] = sortedPreRatioActivCerts.get(0).get(sortedPreRatioActivCerts.get(0).keys().nextElement());
                                twoHighestCertainties[1] = sortedPreRatioActivCerts.get(0).keys().nextElement();
                            }
                            else{
                                twoHighestCertActivities[0] = sortedPreRatioActivCerts.get(sortedPreRatioActivCerts.size()-2).get(sortedPreRatioActivCerts.get(sortedPreRatioActivCerts.size()-2).keys().nextElement());
                                twoHighestCertActivities[1] = sortedPreRatioActivCerts.get(sortedPreRatioActivCerts.size()-1).get(sortedPreRatioActivCerts.get(sortedPreRatioActivCerts.size()-1).keys().nextElement());
                                twoHighestCertainties[0] = sortedPreRatioActivCerts.get(sortedPreRatioActivCerts.size()-2).keys().nextElement();
                                twoHighestCertainties[1] = sortedPreRatioActivCerts.get(sortedPreRatioActivCerts.size()-1).keys().nextElement();
                            }
                        }
                    }
                }
                else{
                    twoHighestCertActivities[0] = sortedCertainties.get(sortedCertainties.size()-2).get(sortedCertainties.get(sortedCertainties.size()-2).keys().nextElement());
                    twoHighestCertActivities[1] = sortedCertainties.get(sortedCertainties.size()-1).get(sortedCertainties.get(sortedCertainties.size()-1).keys().nextElement());
                    twoHighestCertainties[0] = sortedCertainties.get(sortedCertainties.size()-2).keys().nextElement();
                    twoHighestCertainties[1] = sortedCertainties.get(sortedCertainties.size()-1).keys().nextElement();
                }
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in sortActivitiesAndCertByCertDegree: " + e.getMessage());
        }
        return new ActivitiesCertaintiesPairs(new ArrayList<String>(Arrays.asList(twoHighestCertActivities[0], twoHighestCertActivities[1])), new ArrayList<Float>(Arrays.asList(twoHighestCertainties[0], twoHighestCertainties[1])));
    }*/

    protected  static int cardinalOfOnes(ArrayList<Dictionary<Float, String>> certainties){
        int count = 0;
        for(int i=0; i<certainties.size(); ++i){
            if(certainties.get(i).keys().nextElement()>=1.0)
                count++;
        }//        System.out.println("cardinalOfOnes in dictionary "+certainties+"= "+count);
        return count;
    }

    protected  static int cardinalOfOnesInArray(ArrayList<Float> certainties){
        int count = 0;
        for(int i=0; i<certainties.size(); ++i){
            if(certainties.get(i)>= 1.0)
                count++;
        }
        return count;
    }

    protected static ArrayList<Dictionary<Float, String>>  sortActivitiesByIncreasingCertainty(ArrayList<String> activities, ArrayList<Float> certainties) {
        ArrayList<Dictionary<Float, String>> sortedCertainties = new ArrayList<Dictionary<Float, String>>();
        for(int i=0; i<certainties.size(); i++){
            Dictionary<Float, String> d = new Hashtable<Float, String>();
            d.put(certainties.get(i), activities.get(i));
            sortedCertainties.add(d);
        }
        Collections.sort(sortedCertainties, DictionaryComparator);
        return sortedCertainties;
    }

    static ArrayList<Dictionary<String, Float>> sortDictionaryByIncreasingValue(ArrayList<Dictionary<String, Float>> listOfDicts ) {
        System.out.println("Before sorting dictionary by increasing value: "+listOfDicts);
        Collections.sort(listOfDicts, DictionaryComparatorByValue);
        System.out.println("After sorting dictionary by increasing value: "+listOfDicts);
        return listOfDicts;
    }

    static float getStdDev(ArrayList<Integer> array) {
        return (float)Math.sqrt(getVariance(array));
    }

    // Returns detected activities
    static void  onNewEvent(String subActivityID, String subActivity, Double subActivityCertainty, int startFrame, int endFrame, String usedObject, String usedObjectID, float[] objectPos, float[] handObjectDistances) throws InconsistentOntologyException, FuzzyOntologyException{
        currentFrame = endFrame;
        insertSubActivity(subActivityID, subActivity, subActivityCertainty, startFrame, endFrame, usedObject, usedObjectID, objectPos, handObjectDistances);
    }

    static protected boolean criticalTimeHasElapsed(int currentFrame){  // Since last critical activity was detected
        //TODO:Future: account for time restrictions for each activity: medicine cannot happen twice in less than 4h
        // a- Since last criticalActivity (can be the same still being detected) *
        // b- Since last criticalActivity of same type (so we ensure we recognize a given critical activity while it ocurrs only once) at the same time we recognize
        // in real time different types of critical activities.
        //if(detectedCriticalActivities.size()>0){
        if(KBmirrorActivConcept.size()>0){
            //int lastCriticalActivFrame = Integer.parseInt(detectedCriticalActivities.get(detectedCriticalActivities.size()-1).get("frameStamp").toString());  /// **
            int lastActivFrame = KBmirrorActivEndFrames.get(KBmirrorActivEndFrames.size()-1);//System.out.println("Last critical (sub/)activity "+detectedCriticalActivities.get(detectedCriticalActivities.size()-1).get("id")+" occurred at frame: "+ lastCriticalActivFrame);
            if((currentFrame- lastActivFrame) > timeWindowInFrames) //lastCriticalActivFrame) > timeWindowInFrames)
                return true;
            else {
                return false;
            }
        }
        else {
            if((currentFrame-firstFrame)> timeWindowInFrames)
                return true;
            else
                return true; // No critical activity has happened yet and we must listen to them occurring
        }
    }

    static protected boolean isCritical (String concept){
        //if(concept.equals("medicine") || concept.equals("reaching") || concept.equals("moving") ||concept.equals("placing") || concept.equals("opening") || concept.equals("eating") || concept.equals("drinking"))
        //TODO: get it from the ontology: Activity-isCritical-(Boolean)
        if (criticalSubActivities.contains(concept))
            return true;
        else return false;
    }

    static protected ArrayList<String> getActivitiesInvolvingSubActivity(String subActivity){
        ArrayList<String> activities = new ArrayList<String>();
        for (int i=0; i< subActivitiesInvolvedInActivity.size(); ++i)
            if(subActivitiesInvolvedInActivity.get(i).contains(subActivity))
                activities.add(indexes.get(i));
        return activities;
    }

    static protected ArrayList<String> getSubActivitiesInvolvingActivity(String Activity){
        return subActivitiesInvolvedInActivity.get(activities.get(Activity));
    }

    //Returns most recent ID of activity with name "concept"
    static protected String getSubActivityID(String concept){ // NOT GOOD IF MORE THAN ONE!
        for(int i=KBmirrorSubActivities.size()-1; i>=0; i--){
            if (KBmirrorSubActivities.get(i).get("subActivity").equals(concept))
                return (String)KBmirrorSubActivities.get(i).get("subActivityID");
        }
        return "";
    }

    static protected String getActivityID(String concept){
        if (KBmirrorActivConcept.contains(concept)){
            int index = KBmirrorActivConcept.indexOf(concept);
            return KBmirrorActivID.get(index);
        }
        else return "";
    }

    private static int getDataPropertyValue(String conceptName, String dataProperty) throws FuzzyOntologyException,InconsistentOntologyException{
        ///// alternative JUST TO GET ENDFRAME //TODO: generalize
        for (int j=KBmirrorSubActivities.size()-1; j>=0; j--) // We start from the most recent events
            if(KBmirrorSubActivities.get(j).get("subActivity").equals(conceptName))
                return (Integer)KBmirrorSubActivities.get(j).get("endFrame");
        return -1;//startQuerySolution.getSolution();
    }

    private static int getDataPropertyValueFDL(String conceptName, String dataProperty) throws FuzzyOntologyException,InconsistentOntologyException{
        try { //TODO: FIX
            Concept c = kb.getConcept(conceptName);
            AllInstancesQuery q = new AllInstancesQuery(c);
            startQuerySolution = q.solve(kb);
        } catch (FuzzyOntologyException e) {
            System.err.println("Error in getDataPropertyValue: " + e.getMessage());
        }
        return (int)startQuerySolution.getSolution();
    }

    private static void insertFTWAxiomsIntoKB(ArrayList<Dictionary> window) throws InconsistentOntologyException, FuzzyOntologyException{
        try{
            for(int i =0; i< window.size(); ++i){
                // Insert SubActivity instance, objects it uses and start frames.
                String subActivityIndividualName = (String)(window.get(i).get("subActivityID"));
                insertConceptAssertionFDL(subActivityIndividualName, (String)window.get(i).get("subActivity"), defaultDegree);
                insertObjectPropertyAssertionFDL(individualName, subActivityIndividualName, "performsSubActivity", (Double)window.get(i).get("certainty"));
                for(int j=0; j< ((ArrayList<String>)window.get(i).get("objects")).size(); ++j){
                    //System.out.println("Inserting object axiom: "+(String)((ArrayList<String>) window.get(i).get("objectsIDs")).get(j));
                    insertConceptAssertionFDL((String)((ArrayList<String>) window.get(i).get("objectsIDs")).get(j), (String)((ArrayList<String>) window.get(i).get("objects")).get(j), new Double(((ArrayList<Float>)window.get(i).get("objectsCertainties")).get(j)));
                    insertObjectPropertyAssertionFDL(subActivityIndividualName, (String)((ArrayList<String>) window.get(i).get("objectsIDs")).get(j), "usesObject", new Double(((ArrayList<Float>)window.get(i).get("objectsCertainties")).get(j)));
                }
            }
        }catch (Error e){//Catch exception if any
            System.err.println("Error in insertFTWAxiomsIntoKB: " + e.getMessage());
        }
    }

    private static void insertConceptAssertionFDL(String individualName, String concept, Double certainty) throws InconsistentOntologyException, FuzzyOntologyException{
        try{
            Concept activityConcept = kb.getConcept(concept);
            Individual activityIndividual = new Individual(individualName);
            // Concept assertion
            kb.addAssertion(activityIndividual, activityConcept, new DegreeNumeric(defaultDegree));//certDegree); // ** Use uncertainty only in one axiom!!
            //kb.addRelation(userIndividual, "performsActivity", activityIndividual, new DegreeNumeric(certainty)); // THE DIFFERENCE WITH insertSubActivity IS IN THE ROLE NAME
        }catch (Error e){//Catch exception if any
          System.err.println("Error in InsertConceptAssertion FDL: " + e.getMessage());
        }
    }
    private static void insertObjectPropertyAssertionFDL(String individualName, String individualName2, String roleName, Double certainty) throws InconsistentOntologyException, FuzzyOntologyException{
        try{
            //System.out.println("Inserting object property: "+ individualName +" "+roleName+" "+ individualName2);
            Individual individual = kb.getIndividual(individualName);
            //System.out.println("Retrieved individual "+ individual.toString());
            Individual individual2 = kb.getIndividual(individualName2);
            //System.out.println("Retrieved individual2 "+ individual2.toString());
            kb.addRelation(individual, roleName, individual2, new DegreeNumeric(certainty));
            //System.out.println("Added relation "+ roleName);

        }catch (Error e){//Catch exception if any
            System.err.println("Error in insertObjectPropertyAssertionFDL: " + e.getMessage());
        }
    }

    private static double degreeOfCertaintyCloseObjectInteraction(float x){
        //System.out.println("Degree of cert of "+x+": "+ round((float)closeObjectInteraction.getMembershipDegree(x), 2)); // TODO: NO FUNCIONA!!!
        //return round((float)closeObjectInteraction.getMembershipDegree(x), 2);//closeObjectInteraction.getMembershipDegree(round(x, 2));
        if(x < closeObjectInteractionB)
            return 1;
        else return 0;
    }

    private static void insertSubActivity(String subActivityID, String subActivityConceptName, Double certainty, int startFrame, int endFrame, String usedObject, String usedObjectID, float[] objectPos, float[] handObjectDistances) throws InconsistentOntologyException, FuzzyOntologyException{
        try{
            Dictionary subActivity = new Hashtable();
            float touchingCertainty = 0f;
            boolean insertSubAct = false;
            boolean insertObjectInteraction = false;
            Individual subActivityIndividual = new Individual(subActivityID);
            //if(touchDistR<handTouchingObjectDistance3DThreshold || touchDistL<handTouchingObjectDistance3DThreshold ){
            if(KBmirrorSubActivities.size()<1 || !KBmirrorSubActivities.get(KBmirrorSubActivities.size()-1).get("subActivityID").equals(subActivityID)){//lastSubActivityOccurred.get("subActivityID").equals(subActivityID)){ // INSERT NEW COMPLETE ACTIVITY
                insertSubAct = true;
                insertObjectInteraction = true;
                usedObjects.clear();
                usedObjectsIDs.clear();
                objectsPositions.clear();
                usedObjectsCertainties.clear();
            }
            else{ // update object interaction and its position
                touchingCertainty = round((float)degreeOfCertaintyCloseObjectInteraction(Math.min(handObjectDistances[0], handObjectDistances[1])), 2); // TODO: add both if close enough
                if(touchingCertainty > 0.0){ //0.1, 0.3
                    insertObjectInteraction = true;
                } // TODO: add update of positions if  " " " and object is same.
            }
            if(insertSubAct){
                //nSubActivitiesDetected++;
                //Concept subActivityConcept = new Concept(subActivityConceptName);
                // Concept assertion
                //kb.addAssertion(subActivityIndividual, subActivityConcept, new DegreeNumeric(defaultDegree));//certDegree);
                //kb.addRelation(userIndividual, "performsSubActivity", subActivityIndividual, new DegreeNumeric(certainty));
                subActivity.put("subActivity", subActivityConceptName);
                subActivity.put("subActivityID", subActivityID);
                subActivity.put("startFrame", startFrame);
                subActivity.put("endFrame", endFrame);
                subActivity.put("certainty", certainty); // TODO:multiply by mu(FTW, startFrame or endFrame)
                addSubActivToKBMirror(subActivity);
                //System.out.println("Added SubActivity "+subActivityID+" with start-endFrame: " + startFrame + " " + endFrame);
                //System.out.println("Adding subActivID: "+subActivityName + "size of kbMirror "+KBmirrorSubActivities.size());
            }
            if(insertObjectInteraction){
                String objectIndividualName = subActivityID.concat(usedObject).concat(usedObjectID);
                if(!usedObject.equals("") && lastSubActivityOccurred.get("objectsIDs") != null && !((ArrayList<String>)lastSubActivityOccurred.get("objectsIDs")).contains(objectIndividualName)){ // add object assertion and update KBmirror of activities
                    //System.out.println("Touching certainty: "+objectIndividualName);
                    usedObjects.add(usedObject);
                    objectsPositions.add(new ArrayList<Float>(Arrays.asList(objectPos[0], objectPos[1], objectPos[2])));
                    usedObjectsIDs.add(objectIndividualName);
                    usedObjectsCertainties.add(touchingCertainty);
                    //Concept objectConcept = new Concept(usedObject);
                    //Individual objectIndividual = new Individual(usedObject);
                    //kb.addAssertion(objectIndividual, objectConcept, new DegreeNumeric(defaultDegree));//certDegree);
                    //kb.addRelation(subActivityIndividual, "usesObject", objectIndividual, new DegreeNumeric(touchingCertainty));
                    //if(KBmirrorSubActivities.size()>0){
                        KBmirrorSubActivities.get(KBmirrorSubActivities.size()-1).put("objects", usedObjects);
                        KBmirrorSubActivities.get(KBmirrorSubActivities.size()-1).put("objectsIDs", usedObjectsIDs);
                        KBmirrorSubActivities.get(KBmirrorSubActivities.size()-1).put("objectsPositions", objectsPositions);
                        KBmirrorSubActivities.get(KBmirrorSubActivities.size()-1).put("objectsCertainties", usedObjectsCertainties);
                }
                else{ // First Object interaction to be added
                    KBmirrorSubActivities.get(KBmirrorSubActivities.size()-1).put("objects", usedObjects);
                    KBmirrorSubActivities.get(KBmirrorSubActivities.size()-1).put("objectsIDs", usedObjectsIDs);
                    KBmirrorSubActivities.get(KBmirrorSubActivities.size()-1).put("objectsPositions", objectsPositions);
                    KBmirrorSubActivities.get(KBmirrorSubActivities.size()-1).put("objectsCertainties", usedObjectsCertainties);
                    /*subActivity.put("objects", usedObjects); // reemplaza?**
                    subActivity.put("objectsIDs", usedObjectsIDs);
                    subActivity.put("objectsPositions", objectsPositions);
                    subActivity.put("objectsCertainties", usedObjectsCertainties); //*/
                }
                //System.out.println("Adding object: "+subActivityName + " size of kbMirror "+KBmirrorSubActivities.size());
            }
            lastSubActivityOccurred = KBmirrorSubActivities.get(KBmirrorSubActivities.size()-1);
        }catch (Exception e){//Catch exception if any
          System.err.println("Error in InsertSubActivity: " + e.getMessage());
        }
    }

    public static String removeQuotes(String str){
        //String s = new String(str.substring(1, str.length()-2));
        char ch = '\0';
        String s = str.replace("\"", "");
        return s;//register[i] = register[i].substring(1,(register.length-1));
    }

    public static void addSubActivToKBMirror(Dictionary newSubActivity){
       KBmirrorSubActivities.add(newSubActivity);
    }

    public static void addActivToKBMirror(String activityID, String activity, int startFrame, int endFrame){
        if(KBmirrorActivConcept.contains(activity)){
            int index = KBmirrorActivConcept.indexOf(activity);
            KBmirrorActivID.set(index, activityID);
            KBmirrorActivConcept.set(index, activity);
            KBmirrorActivStartFrames.set(index, startFrame);
            KBmirrorActivEndFrames.set(index, endFrame);
        }
        else{
            KBmirrorActivID.add(activityID);
            KBmirrorActivConcept.add(activity);
            KBmirrorActivStartFrames.add(startFrame);
            KBmirrorActivEndFrames.add(endFrame);
        }
    }

    public static String getNaiveActivityWithHighestCertaintyOverThreshold(ArrayList<Float> certainties, ArrayList<String> candidateActivities, float minActivDetectionThreshold, ActivitiesCertaintiesPairs activitiesCertaintiesPairsSecondRatio, ActivitiesCertaintiesPairs activitiesCertaintiesPairsPreRatio){
        String activity = "";
        ArrayList<String> filteredActivities = new ArrayList<String>();
        ArrayList<Float> filteredCertainties = new ArrayList<Float>();
        ArrayList<String> filteredActivities2 = new ArrayList<String>();
        ArrayList<Float> filteredCertainties2 = new ArrayList<Float>();
        try{
            // 1. FILTER THE ACTIVITIES WITH HIGHEST CERTAINTY -over the threshold- TO BE HAPPENING
            System.out.print("\n - Activities over Act.Cert.Threshold: ");
            for (int i=0; i<certainties.size(); ++i){
                if (certainties.get(i) >= minActivDetectionThreshold){
                    System.out.print(candidateActivities.get(i)+ "("+certainties.get(i) + ") ");
                    filteredActivities.add(candidateActivities.get(i));
                    filteredCertainties.add(certainties.get(i));
                }
            }
            //if(takeHighestCertaintyActivity || filteredActivities.size()>1){ // TAKE HIGHEST CERTAINTY ACTIVITY FROM REMAINED FILTERED ONES (EXCLUDING STACKING AND UNSTACKING)// TODO:Check if they shouldnt be removed /fulfillingExtraConstr is working
            if (filteredActivities.size()<1){
                System.out.print("\n and over secondaryActivThreshold: ");//**** ObjPosConstraints passed by (max.value over secondaryActivThresh.): ");
                for (int i=0; i<certainties.size(); ++i){
                    if(certainties.get(i)>= activityDetectionSecondaryThreshold){// && filteredCertainties.get(i) > max){
                        System.out.print(candidateActivities.get(i)+ "("+certainties.get(i) + ") ");
                        filteredActivities2.add(candidateActivities.get(i));
                        filteredCertainties2.add(certainties.get(i));
                        //max = filteredCertainties.get(i);
                        //activity = filteredActivities.get(i);
                    }
                }
            }
            else{
                filteredActivities2 = filteredActivities;
                filteredCertainties2 = filteredCertainties;
            }

            if(filteredActivities2.size()>0){
                if(filteredActivities2.size() == 1)
                    return filteredActivities2.get(0);
                else{
                    System.out.println("\n****** Naive approach: returning highest certainty activity: ");
                    ArrayList<Dictionary<Float, String>> sortedActivAndCert = sortActivitiesByIncreasingCertainty(filteredActivities2, filteredCertainties2);
                    if(sortedActivAndCert.size() >0){
                        float cert = sortedActivAndCert.get(sortedActivAndCert.size()-1).keys().nextElement();
                        activity = sortedActivAndCert.get(sortedActivAndCert.size()-1).get(cert);
                        System.out.println(activity);
                        return activity;
                    }
                }
            }
        }
        catch (Exception e){
            System.err.println("Error in getNaiveActivityWithHighestCertaintyOverThreshold: " + e.getMessage());//oh noes!
        }
        return activity;
    }

    public static String getActivityWithHighestCertaintyOverThresholdAndObjPosConstraints(ArrayList<Float> certainties, ArrayList<String> candidateActivities, float minActivDetectionThreshold, ActivitiesCertaintiesPairs activitiesCertaintiesPairsSecondRatio, ActivitiesCertaintiesPairs activitiesCertaintiesPairsPreRatio){
        String activity = "";
        ArrayList<String> filteredActivities = new ArrayList<String>();
        ArrayList<Float> filteredCertainties = new ArrayList<Float>();
        ArrayList<String> filteredActivities2 = new ArrayList<String>();
        ArrayList<Float> filteredCertainties2 = new ArrayList<Float>();
        try{
        // 1. FILTER THE ACTIVITIES WITH HIGHEST CERTAINTY -over the threshold- TO BE HAPPENING
            System.out.print("\n - Activities over Act.Cert.Threshold: ");
            for (int i=0; i<certainties.size(); ++i){
                if (certainties.get(i) >= minActivDetectionThreshold){
                    System.out.print(candidateActivities.get(i)+ "("+certainties.get(i) + ") ");
                    filteredActivities.add(candidateActivities.get(i));
                    filteredCertainties.add(certainties.get(i));
                }
            }

            //if(takeHighestCertaintyActivity || filteredActivities.size()>1){ // TAKE HIGHEST CERTAINTY ACTIVITY FROM REMAINED FILTERED ONES (EXCLUDING STACKING AND UNSTACKING)// TODO:Check if they shouldnt be removed /fulfillingExtraConstr is working
            if (filteredActivities.size()<1){
                System.out.print("\n and over secondaryActivThreshold: ");//**** ObjPosConstraints passed by (max.value over secondaryActivThresh.): ");
                for (int i=0; i<certainties.size(); ++i){
                    if(certainties.get(i)>= activityDetectionSecondaryThreshold){// && filteredCertainties.get(i) > max){
                        System.out.print(candidateActivities.get(i)+ "("+certainties.get(i) + ") ");
                        filteredActivities2.add(candidateActivities.get(i));
                        filteredCertainties2.add(certainties.get(i));
                        //max = filteredCertainties.get(i);
                        //activity = filteredActivities.get(i);
                    }
                }
            }
            else{
                filteredActivities2 = filteredActivities;
                filteredCertainties2 = filteredCertainties;
            }

            if(filteredActivities2.size()>0){
                if(filteredActivities2.size() == 1)
                    return filteredActivities2.get(0);
                else{
                    System.out.println("\n****** Final Subsumption check for highest certainties with diff < "+ minDiffForConsideringDraw+ ": ");
                    if(activitiesCertaintiesPairsSecondRatio != null){
                        String act = CheckActivSubsumptionAndReturnHighestCertActiv(filteredCertainties2, filteredActivities2, activitiesCertaintiesPairsSecondRatio.getCertainties(), activitiesCertaintiesPairsSecondRatio.getActivities(), activitiesCertaintiesPairsPreRatio.getCertainties(), activitiesCertaintiesPairsPreRatio.getActivities());
                        System.out.print(" *******"+act); // TODO: CHECK MIN LOW-THRESHOLD,optimize in one line
                        return act;
                    }
                }
            }
            else{
                // Second opportunity if all certainties are zero
                if(activitiesCertaintiesPairsSecondRatio != null){
                    if(activitiesCertaintiesPairsSecondRatio.getActivities().size()>0){
                        ArrayList<Dictionary<Float, String>> sortedActivAndCert = sortActivitiesByIncreasingCertainty(activitiesCertaintiesPairsSecondRatio.getActivities(), activitiesCertaintiesPairsSecondRatio.getCertainties());
                        if(sortedActivAndCert.size() >0){
                            float cert = sortedActivAndCert.get(sortedActivAndCert.size()-1).keys().nextElement();
                            if(cert >= activityPreRatioDetectionThreshold){
                                activity = sortedActivAndCert.get(sortedActivAndCert.size()-1).get(cert);
                                System.out.println("2nd Opportunity: returning highest ratio activity over thresh.: "+activity);
                                return activity;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e){
            System.err.println("Error in getActivityWithHighestCertaintyOverThresholdAndObjPosConstraints: " + e.getMessage());//oh noes!
        }
        return activity;
    }

    static protected String getSuperiorSubsumptionClassBySALength(ArrayList<String> highestCertActivities){
        String activity = "";
        ArrayList<String> highestCertActivitiesOriginal = (ArrayList<String>)highestCertActivities.clone();
        try{ // CHECK FIRST EQUAL SUBSUMPTION (SYMMETRIC SUBSUMPTION PAIRS OF ACTIVITIES):
            if(highestCertActivities.contains(STACKING) || highestCertActivities.contains(UNSTACKING)){
                //FILTER THE ACTIVITIES FULFILLING OBJECT POSITION RESTRICTIONS
                System.out.print("[ ObjPosConstraints passed by: ");
                if(highestCertActivities.contains(STACKING)){ // TODO: also !UNSTACKING?
                    ArrayList<Dictionary> FTW = cutFTWInNrOfSubActFromLatestSubActivitiesForActivity(STACKING);
                    if(FTWFulfillsObjectPositions(FTW, STACKING)){//!activity.equals("")) // TODO Return with final certainty
                        System.out.print("STACKING ] \n");
                        return STACKING;
                    }else{    //highestCertActivities.remove(highestCertActivities.indexOf(STACKING));
                        highestCertActivities.remove(STACKING);
                    }
                }
                if(highestCertActivities.contains(UNSTACKING)){
                    ArrayList<Dictionary> FTW = cutFTWInNrOfSubActFromLatestSubActivitiesForActivity(UNSTACKING);
                    //activity = getActivityFulfillingExtraConstraintsInFTW(FTW, candidateActivities);
                    if(FTWFulfillsObjectPositions(FTW, UNSTACKING)){//!activity.equals(""))
                        System.out.print("UNSTACKING ] \n");
                        return UNSTACKING;
                    }
                    else{ //  highestCertActivities.remove(highestCertActivities.indexOf(UNSTACKING));
                        highestCertActivities.remove(UNSTACKING);
                    }
                }
                if(highestCertActivities.size()<1){
                    System.out.print("NONE (returning previous candidate: "+highestCertActivitiesOriginal.get(highestCertActivitiesOriginal.size()-1)+") ]\n");//                    highestCertActivities = candidateActivities;
                    return highestCertActivitiesOriginal.get(highestCertActivitiesOriginal.size()-1);//activity;  // TODO: check for more activities
                }
            }
            else{
                if(highestCertActivities.contains(STACKING))
                    highestCertActivities.remove(STACKING);
                if(highestCertActivities.contains(UNSTACKING))
                    highestCertActivities.remove(UNSTACKING);
                for(int i=highestCertActivities.size()-1; i>=0; --i){
                    ArrayList<String> restOfActivities = (ArrayList<String>)highestCertActivities.clone();
                    restOfActivities.remove(i);
                    if(subsumesOtherActivityInList(highestCertActivities.get(i), restOfActivities, activitiesDurationsInNrOfSubActivities)){
                        System.out.println("activ. subsuming other in the list: "+highestCertActivities.get(i)+"\n");
                        return highestCertActivities.get(i);
                    }
                }
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in getSuperiorSubsumptionClassBySALength: " + e.getMessage());
        }
        return activity;
    }

    static protected Boolean subsumesOtherActivityInList(String activity, ArrayList<String> restOfActivities, ArrayList<Integer> activitiesDurationsInNrOfSubActivities){
        boolean subsumes = true;
        try{
            for(int i=0; i<restOfActivities.size() && subsumes; ++i){
                if(activitiesDurationsInNrOfSubActivities.get(activities.get(activity))< activitiesDurationsInNrOfSubActivities.get(activities.get(restOfActivities.get(i))))
                    subsumes = false;
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in subsumesOtherActivityInList: " + e.getMessage());
        }
        return subsumes;
    }

    static protected  String getHighestCertPreRatioActivityThanRatio(float ratio, ArrayList<Float> preRatioFilterRatios, ArrayList<String> preRatioFilterActivities){
        String activity = "";
        float maxRatio = ratio;
        try{
            for(int i=0; i<preRatioFilterRatios.size(); ++i){
                if(preRatioFilterRatios.get(i)> maxRatio){
                    activity = preRatioFilterActivities.get(i);
                    maxRatio = preRatioFilterRatios.get(i);
                }
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in getHighestCertPreRatioActivityThanRatio: " + e.getMessage());
        }
        return activity;
    }

    static protected String containsAWinningSubsumptionPair(ArrayList<String> highestCertActivities, ArrayList<ArrayList<String>> subsumesPairsBySubAct, ArrayList<Float> preRatioFilterRatios, ArrayList<String> preRatioFilterActivities){
        String activity = "";
        String highestPreRatioActivity = "";
        try{
            ArrayList<Float> filteredPreRatioFilterRatios = new ArrayList<Float>();//ArrayList<Float>)preRatioFilterRatios.clone();
            ArrayList<String> filteredPreRatioFilterActivities = new ArrayList<String>();//ArrayList<String>)preRatioFilterActivities.clone();
            //System.out.println("PreRatios Before: "+preRatioFilterActivities+" "+preRatioFilterRatios);
            for(int i=0; i<highestCertActivities.size(); ++i){
                filteredPreRatioFilterRatios.add(preRatioFilterRatios.get(preRatioFilterActivities.indexOf(highestCertActivities.get(i))));
                filteredPreRatioFilterActivities.add(highestCertActivities.get(i));
            }
            //System.out.println("PreRatios After: "+filteredPreRatioFilterActivities+" "+filteredPreRatioFilterRatios);

            for(int i=0; i<subsumesPairsBySubAct.size(); ++i){
                if(highestCertActivities.containsAll(subsumesPairsBySubAct.get(i))){
                    //System.out.println("Subsumption pair included: "+subsumesPairsBySubAct.get(i).get(0)+" "+subsumesPairsBySubAct.get(i).get(1));
                    if(filteredPreRatioFilterRatios.get(filteredPreRatioFilterActivities.indexOf(subsumesPairsBySubAct.get(i).get(0))) < filteredPreRatioFilterRatios.get(filteredPreRatioFilterActivities.indexOf(subsumesPairsBySubAct.get(i).get(1)))){// Return the highest ('subsumer') superclass (the second element of the pair)
                        highestPreRatioActivity = getHighestCertPreRatioActivityThanRatio(filteredPreRatioFilterRatios.get(filteredPreRatioFilterActivities.indexOf(subsumesPairsBySubAct.get(i).get(1))), filteredPreRatioFilterRatios, filteredPreRatioFilterActivities);
                        if(highestPreRatioActivity.equals("")) { // There is no activity with highest preRatio than the selected one
                            activity =  subsumesPairsBySubAct.get(i).get(1);
                            System.out.println(subsumesPairsBySubAct.get(i).get(1)+"(preRatio "+filteredPreRatioFilterRatios.get(filteredPreRatioFilterActivities.indexOf(subsumesPairsBySubAct.get(i).get(1)))+") subsumes "+ subsumesPairsBySubAct.get(i).get(0)+"("+ filteredPreRatioFilterRatios.get(filteredPreRatioFilterActivities.indexOf(subsumesPairsBySubAct.get(i).get(0)))+")!");
                            return activity;
                        }
                        /*else{ // Return the activity with highest preRatio
                            System.out.println("returning highest preRatio activ: "+highestPreRatioActivity);
                            return highestPreRatioActivity;
                        }*/
                    }
                    else{
                        highestPreRatioActivity = getHighestCertPreRatioActivityThanRatio(filteredPreRatioFilterRatios.get(filteredPreRatioFilterActivities.indexOf(subsumesPairsBySubAct.get(i).get(0))), filteredPreRatioFilterRatios, filteredPreRatioFilterActivities);
                        if(highestPreRatioActivity.equals("")) { // There is no activity with highest preRatio than the selected one
                            activity =  subsumesPairsBySubAct.get(i).get(0);
                            System.out.println(subsumesPairsBySubAct.get(i).get(0)+"(preRatio "+filteredPreRatioFilterRatios.get(filteredPreRatioFilterActivities.indexOf(subsumesPairsBySubAct.get(i).get(0)))+") Subsumes "+ subsumesPairsBySubAct.get(i).get(1)+"("+ filteredPreRatioFilterRatios.get(filteredPreRatioFilterActivities.indexOf(subsumesPairsBySubAct.get(i).get(1)))+")!");
                            return activity;
                        }
                        /*else{ // Return the activity with highest preRatio
                            System.out.println("Returning highest preRatio activ: "+highestPreRatioActivity);
                            return highestPreRatioActivity;
                        }*/
                    }
                }
            }
            //IF NONE OF THE SUBSUMPTION PAIRS APPEAR  // TODO: remove and return ""?
            //System.out.println("Returning highest preRatio activ: "+highestPreRatioActivity);
            //return highestPreRatioActivity;
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in containsAWinningSubsumptionPair: " + e.getMessage());
        }
        return activity; //TODO Return highestPreRatioActiv? use only one!
    }

    static protected String fulfillsActivitysDistinctiveObject(ArrayList<String> candidateActivities, ArrayList<String> preRatioActivities, ArrayList<Float> preRatioRatios, ArrayList<String> secondRatioActivities, ArrayList<Float> secondRatioRatios){
        Boolean fulfills = false;
        String activity = "";
        //String maxOverallRateActiv = "";
        Boolean applyOverallRating = false;
        ArrayList<Float> overallRatios = new ArrayList<Float>();
        //int nOfChallengingSimilarActivities =0;
        try{
            for(int i=0; i<candidateActivities.size(); ++i)
                if(groupsOfChallengingSimilarActivities.contains(candidateActivities.get(i))){
                    //nOfChallengingSimilarActivities++;
                    applyOverallRating = true;
                }

            if(applyOverallRating){
                for(int i=0; i< candidateActivities.size(); ++i){
                    String act = candidateActivities.get(i);
                    // compute also if its overall ratio is higher than the rest of activities
                    float preRatio = preRatioRatios.get(preRatioActivities.indexOf(act));
                    float orderObjRatio = getOrderAndObjectConstraintsFuzzyRatio(act);
                    float orderObjSubSeqRatio = getOrderAndObjectSubSequenceConstraintsRatio(act);
                    float overallRatio = (preRatio + orderObjRatio + orderObjSubSeqRatio)/3;
                    //float overallRatio = (getActivityPreRatioForContainedSubActivityAndObjectConstraints(highestCertActivities.get(j)) + getOrderAndObjectConstraintsFuzzyRatio(highestCertActivities.get(j)) + getOrderAndObjectSubSequenceConstraintsRatio(highestCertActivities.get(j)))/3;//+ getMinDifferentObjCategoriesRequiredRatio(highestCertActivities.get(j)))/4;
                    overallRatios.add(round(overallRatio, 2));
                }
                System.out.println("Candidate activities: "+candidateActivities);
                System.out.println("Overall ratios: "+overallRatios);

                ArrayList<Dictionary<Float, String>> orderedActivities = sortActivitiesByIncreasingCertainty(candidateActivities, overallRatios);
                float maxOverallRate;
                String activityWithHighestOverallRate;

                if(orderedActivities.size()>1){
                    float penultimateOverallRate = orderedActivities.get(orderedActivities.size()-2).keys().nextElement();
                    float lastOverallRate = orderedActivities.get(orderedActivities.size()-1).keys().nextElement();
                    float difference = lastOverallRate-penultimateOverallRate;
                    if(difference > 0.15){
                        maxOverallRate = getMaxFloat(overallRatios);
                        activityWithHighestOverallRate = candidateActivities.get(overallRatios.indexOf(maxOverallRate));
                        if(!activityWithHighestOverallRate.equals(distinguishableAmongChallengingActivities.get(0))){
                            //Remove first distinctive activity (CLEANING IN THIS CASE)
                            overallRatios.remove(candidateActivities.indexOf(distinguishableAmongChallengingActivities.get(0)));
                            candidateActivities.remove(distinguishableAmongChallengingActivities.get(0));
                            maxOverallRate = getMaxFloat(overallRatios);
                            activityWithHighestOverallRate = candidateActivities.get(overallRatios.indexOf(maxOverallRate));
                        }
                        else activityWithHighestOverallRate = distinguishableAmongChallengingActivities.get(0);

                        System.out.println(" NOT DISTINCTIVE (Returning highestOverallRate)");
                        return activityWithHighestOverallRate;
                    }
                }
                if(candidateActivities.contains(distinguishableAmongChallengingActivities.get(0))){ //TODO associate condition to activity //Here cleanMicroOrCloth
                    ArrayList<Dictionary> activitiesOccurredInLastFTW = cutFTWInNrOfSubActFromLatestSubActivitiesForActivity(distinguishableAmongChallengingActivities.get(0));
                    for(int i=0; i<activitiesOccurredInLastFTW.size() && !fulfills; ++i){
                        if((activitiesOccurredInLastFTW.get(i).get("subActivity")).equals(CLEANING)){
                            //System.out.println(" + ");
                            if((((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objects")).contains(CLOTH)) && ((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objects")).contains(MICROWAVE)){
                                System.out.println(" DISTINCTIVE! ");
                                activity = CLEANING_OBJECTS;
                                fulfills = true;
                            }
                        }
                        if(activity.equals("")){
                            System.out.println(" NOT DISTINCTIVE 2 (Returning highestOverallRate)");
                            activity = orderedActivities.get(orderedActivities.size()-1).get(orderedActivities.get(orderedActivities.size()-1).keys().nextElement());
                        }
                    }
                }
                else{
                    // return the highest overall rate activity
                    System.out.println(" NOT DISTINCTIVE 3 (Returning highestOverallRate)");
                    activity = orderedActivities.get(orderedActivities.size()-1).get(orderedActivities.get(orderedActivities.size()-1).keys().nextElement());
                }
                return activity;
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in fulfillsActivitysDistinctiveObject: " + e.getMessage());
        }
        return activity;
    }

    static protected String CheckActivSubsumptionAndReturnHighestCertActiv(ArrayList<Float> certainties, ArrayList<String> candidateActivities, ArrayList<Float> secondRatioFilterRatios, ArrayList<String> secondRatioFilterActivities, ArrayList<Float> preRatioFilterRatios, ArrayList<String> preRatioFilterActivities) {
        try{
            // Sort first the activities by certainty
            //TODO: useless? ActivitiesCertaintiesPairs highestCertActivitiesAndCertainties = sortActivitiesAndCertByCertDegree(certainties, candidateActivities, secondRatioFilterRatios, secondRatioFilterActivities, preRatioFilterRatios, preRatioFilterActivities); // TODO generalize for +2 draws-> need for entailment?(SWRL) rule to apply general in all levels
            ArrayList<Dictionary<Float, String>> activAndCertainties = sortActivitiesByIncreasingCertainty(candidateActivities, certainties); // TODO optimize datastructure transformation?
            ArrayList<String> highestCertActivities = new ArrayList<String>();//highestCertActivitiesAndCertainties.getActivities();
            ArrayList<Float> highestCertCertainties = new ArrayList<Float>();//highestCertActivitiesAndCertainties.getCertainties();
            //int ones = cardinalOfOnesInArray(highestCertCertainties);
            //ArrayList<String> onesActivities = new ArrayList<String>();
            //ArrayList<Float> onesCertainties = new ArrayList<Float>();
            for(int i=0; i< activAndCertainties.size(); ++i){
                float certainty = activAndCertainties.get(i).keys().nextElement();
                String activity = activAndCertainties.get(i).get(certainty);
                highestCertCertainties.add(certainty);
                highestCertActivities.add(activity);
                //System.out.print(activity+ " ");
                /*if(certainty >= 1.0f){
                    onesCertainties.add(1.0f);
                    onesActivities.add(activity);
                }*/
            }
            // Largest OVERALL RATIO: If none of the activities contains other, we take the one with largest overall ratio
            ArrayList<String> finalCandidateActivities = new ArrayList<String>();
            ArrayList<Float> finalCertainties = new ArrayList<Float>();
            if(!highestCertActivities.get(highestCertActivities.size()-1).equals("") && !highestCertActivities.get(highestCertActivities.size()-2).equals("")){// TODO needed?  always 2 at least?
                float certDifference = Math.abs(highestCertCertainties.get(highestCertCertainties.size()-1) -highestCertCertainties.get(highestCertCertainties.size()-2));
                if (cardinalOfOnesInArray(highestCertCertainties) > 1 || certDifference < minDiffForConsideringDraw) {
                    System.out.println("'Ones' Draw--> Returning: ");//highest overallRatio activity from: ");
                    // 0- Check if the finalist activities contain one with distinctive SA or Object
                    String distinctiveActivity = fulfillsActivitysDistinctiveObject(highestCertActivities, preRatioFilterActivities, preRatioFilterRatios, secondRatioFilterActivities, secondRatioFilterRatios);
                    if(!distinctiveActivity.equals("")){
                        System.out.print("activ. fulfilling distinctive Obj: " + distinctiveActivity);
                        return distinctiveActivity;
                    }
                    else{// 1-Check subsumption by SA
                        String winningActivity = containsAWinningSubsumptionPair(highestCertActivities, subsumesPairsBySubAct, preRatioFilterRatios, preRatioFilterActivities);
                        if(!winningActivity.equals("")){
                            System.out.print("highest subsumption Act. by SA: " + winningActivity);
                            return winningActivity;
                        }
                        else{// 2-Check subsumption by SA length
                            winningActivity = getSuperiorSubsumptionClassBySALength(highestCertActivities);
                            if(!winningActivity.equals("")){
                                System.out.print("highest subsumption Act. by length(SA): " + winningActivity);
                                return winningActivity;
                            }
                            else{ // 3- Return highest Overall Ratio activity
                                if(highestCertActivities.size()==1){
                                    System.out.print("activ. left (not fulfilling subsump. cond.): "+highestCertActivities.get(0)+" ] ");
                                    return highestCertActivities.get(0);
                                }
                                else{
                                    System.out.print("highest overallRatio Act. from: ");
                                    for (int j = 0; j < highestCertActivities.size(); ++j) {
                                        float preRatio = getActivityPreRatioForContainedSubActivityAndObjectConstraints(highestCertActivities.get(j));
                                        float orderObjRatio = getOrderAndObjectConstraintsFuzzyRatio(highestCertActivities.get(j));
                                        float orderObjSubSeqRatio = getOrderAndObjectSubSequenceConstraintsRatio(highestCertActivities.get(j));
                                        float overallRatio = (preRatio + orderObjRatio + orderObjSubSeqRatio)/3;
                                        //float overallRatio = (getActivityPreRatioForContainedSubActivityAndObjectConstraints(highestCertActivities.get(j)) + getOrderAndObjectConstraintsFuzzyRatio(highestCertActivities.get(j)) + getOrderAndObjectSubSequenceConstraintsRatio(highestCertActivities.get(j)))/3;//+ getMinDifferentObjCategoriesRequiredRatio(highestCertActivities.get(j)))/4;
                                        overallRatio = round(overallRatio, 2); // TODO- put in method getOverallRatioOfActivityBeingHappening
                                        System.out.print(highestCertActivities.get(j) +" Pre: "+preRatio+" OrdObj: "+orderObjRatio+" OrdObjSubSeq: "+orderObjSubSeqRatio+ "(Overall: " + overallRatio + ") ");
                                        finalCertainties.add(overallRatio);
                                        finalCandidateActivities.add(highestCertActivities.get(j));
                                    }
                                    ArrayList<Dictionary<Float, String>> orderedOverallRatios = sortActivitiesByIncreasingCertainty(finalCandidateActivities, finalCertainties);
                                    return orderedOverallRatios.get(orderedOverallRatios.size() - 1).get(orderedOverallRatios.get(orderedOverallRatios.size() - 1).keys().nextElement());
                                }
                            }
                        }
                    }
                }
                else
                    return highestCertActivities.get(highestCertActivities.size() - 1); // ordered in ascending order, thus we take the last element
            }
            else{ // TODO unreachable?
                if(!highestCertActivities.get(highestCertActivities.size()-1).equals("")){
                    System.out.println("Only one candidate Activ.: "+highestCertActivities.get(highestCertActivities.size()-1));
                    return highestCertActivities.get(highestCertActivities.size()-1);
                }
                else{
                    if(!highestCertActivities.get(highestCertActivities.size()-2).equals("")){
                        System.out.println("Only one candidate Activ.: "+highestCertActivities.get(highestCertActivities.size()-2));
                        return highestCertActivities.get(highestCertActivities.size()-2);
                    }
                    else{ // both activities are ""
                        System.out.println("No candidate Activ. ");
                        return "";
                    }
                }
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in CheckActivSubsumptionAndReturnHighestCertActiv: " + e.getMessage());
        }
        System.out.println("Returning NULL Activ.");
        return "";//getMaxDegreeActivity(certainties, candidateActivities);
    }

    static protected Boolean containsAnyOfTheRestOfActivities (String activity, ArrayList<String> restOfActivities){ // containedIn
        try{
            if(activity.equals("") || restOfActivities.size()==0)
                return false;
            else{
                for(int i=0; i<restOfActivities.size(); ++i)
                    if(containedIn(restOfActivities.get(i), activity))
                        return true;
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in containsAnyOfTheRestOfActivities: " + e.getMessage());
        }
        return false;
    }

    static protected Boolean containedIn (String activity1, String activity2){
        boolean exit = false;
        boolean found = false;
        try{
            if(activity1.equals("") || activity2.equals(""))
                return false;
            else{
                ArrayList<HashMap<String, ArrayList<String>>> subActsFromActiv1 = new ArrayList<HashMap<String, ArrayList<String>>>();
                subActsFromActiv1 = (ArrayList<HashMap<String, ArrayList<String>>>) orderObjectsConstraints.get(activities.get(activity1)).clone();
                ArrayList<HashMap<String, ArrayList<String>>> subActsFromActiv2 = new ArrayList<HashMap<String, ArrayList<String>>>();
                subActsFromActiv2 = (ArrayList<HashMap<String, ArrayList<String>>>) orderObjectsConstraints.get(activities.get(activity2)).clone();
                for(int i=0; i<subActsFromActiv1.size() && !exit; ++i){
                    found = false;
                    int j=0;
                    while(j <subActsFromActiv2.size() && !found){//for(int j=0; j<subActsFromActiv2.size() && !found; ++j){
                        String SA1 = subActsFromActiv1.get(i).keySet().iterator().next();
                        String SA2 = subActsFromActiv2.get(j).keySet().iterator().next();
                        if(SA1.equals(SA2)){
                            found = true;
                            subActsFromActiv2.remove(j);
                        }
                        else ++j;
                    }
                    if (!found)
                        exit = true;
                }
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in containedIn: " + e.getMessage());
        }
        return found;
    }

    static protected Boolean FTWFulfillsObjectPositions (ArrayList<Dictionary> activitiesOccurredInLastFTW, String activityToMatch){
        // TODO Checks for and objects relative positions, spacedOrder.   // TODO: reuse calculation of FTW
        try{
            boolean exit;
            float[] pos1; // Positions of objects hold with left and right hands
            float[] pos2;
            int i;
            String object1, object2; // objects held with each hand
            ArrayList<Dictionary> objectsInStackedPosition = new ArrayList<Dictionary>();
            ArrayList<Dictionary> objectsInUnstackedPosition = new ArrayList<Dictionary>();
            String object1ID, object2ID;
            i= 0;
            exit = false;
            //String state = "";   // Check closeness with previously put together objects of same type until there are 3.//fuzzificar
            while (i < (activitiesOccurredInLastFTW.size()-1) ){// && !exit){ // SubActivities in a pattern
                for(int h=0; h<((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objects")).size()-1 && !exit; ++h){ // quitar el -1?
                    if(h==0){
                        object1 = ((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objects")).get(h);
                        object2 = ((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objects")).get(h+1);
                        object1ID = ((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objectsIDs")).get(h);
                        object2ID = ((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objectsIDs")).get(h+1);
                        pos1 = new float[]{((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h).get(0), ((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h).get(1), ((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h).get(2)};
                        pos2 = new float[]{((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h+1).get(0), ((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h+1).get(1), ((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h+1).get(2)};
                    }
                    else{
                        object1 = ((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objects")).get(h-1);
                        object2 = ((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objects")).get(h); // TODO: trunk to int for efficiency
                        object1ID = ((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objectsIDs")).get(h-1);
                        object2ID = ((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objectsIDs")).get(h);
                        pos1 = new float[]{((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h-1).get(0), ((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h-1).get(1), ((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h-1).get(2)};
                        pos2 = new float[]{((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h).get(0), ((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h).get(1), ((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h).get(2)};
                    }
                    if(objectBelongsToCategory(object1, STACKABLE) && objectBelongsToCategory(object2, STACKABLE) && object1.equals(object2)){ // NEEDED?
                        // CHECKING FOR STACKED OBJECTS   //TODO:define with SWRL rules?/MAMDANI?
                        if(objectsOnTopOfEachOther(pos1, pos2) && objectsInStackedPosition.size()<1){//state.equals("stacked")){ //
                            Dictionary o1 = new Hashtable();
                            o1.put("object", object1); // IDS? para que sean distintos?
                            o1.put("pos", new float[]{pos1[0], pos1[1], pos1[2]});
                            o1.put("objectID", object1ID);
                            objectsInStackedPosition.add(o1);
                            Dictionary o2 = new Hashtable();
                            o2.put("object", object2);
                            o2.put("pos", new float[]{pos2[0], pos2[1], pos2[2]});
                            o2.put("objectID", object2ID);
                            objectsInStackedPosition.add(o2);
                        }
                        else{
                            if(objectsOnTopOfEachOther(pos1, pos2) && objectsInStackedPosition.size()>1){ // SEARCHING MIN 3 OBJECTS OF SAME TYPE ARE DETECTED WITH "STACKED" POSITIONS
                                //System.out.println("__StackingMov__ detected in activ "+activityToMatch);
                                Dictionary o2 = new Hashtable();
                                o2.put("object", object2);
                                o2.put("pos", new float[]{pos2[0], pos2[1], pos2[2]});
                                o2.put("objectID", object2ID);
                                objectsInStackedPosition.add(o2);
                                if(activityToMatch.equals(STACKING))
                                    return true;
                            }
                            else{   // CHECKING FOR UNSTACKED OBJECTS
                                if(objectsOnSameSurface(pos1, pos2) && objectsInUnstackedPosition.size()<1){//state.equals("stacked")){ //
                                    Dictionary o1 = new Hashtable();
                                    o1.put("object", object1);
                                    o1.put("pos", new float[]{pos1[0], pos1[1], pos1[2]});
                                    o1.put("objectID", object1ID);
                                    objectsInUnstackedPosition.add(o1); // if different type than the previous object?
                                    Dictionary o2 = new Hashtable();
                                    o2.put("object", object2);
                                    o2.put("pos", new float[]{pos2[0], pos2[1], pos2[2]});
                                    o2.put("objectID", object2ID);
                                    objectsInUnstackedPosition.add(o2);
                                }
                                else{ // TODO: IS CONSIDERING STATES OF GROUPS OF 3 OBJECTS OF SAME TYPE NEEDED?
                                    if(objectsOnSameSurface(pos1, pos2) && objectsInUnstackedPosition.size()>1){ // SEARCHING MIN 3 OBJECTS OF SAME TYPE THAT ARE DETECTED WITH "UNSTACKED" POSITIONS
                                        //System.out.println("__UnstackingMov__ detected in activ "+activityToMatch);
                                        Dictionary o2 = new Hashtable();
                                        o2.put("object", object1);
                                        o2.put("pos", new float[]{pos1[0], pos1[1], pos1[2]});
                                        o2.put("objectID", object1ID);
                                        objectsInUnstackedPosition.add(o2);
                                        // SAME TYPE  // && !previousObjectID.equals(o1.get("objectID")
                                        if(activityToMatch.equals(UNSTACKING))
                                            return true;
                                    }
                                }
                            }
                        }
                    }
                }
                i++;
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in filterActivitiesFulfillingExtraConstraints: " + e.getMessage());
        }
        return false;
    }

    public static String LearnWeightsAndCreateRules(String fileToLearn, ArrayList<Integer> usingFoldIDs, String pathToWriteRules){
        String rules = "\n";
        try{
            FileInputStream fstream = new FileInputStream(fileToLearn);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            //SubActivities occurrence rates
            int [][] subActivitiesRates = new int[activities.size()][SubActivities.size()];
            for (int i=0; i< subActivitiesRates.length; ++i)
                for (int j =0; j<subActivitiesRates[i].length; j++)
                    subActivitiesRates[i][j] = 0;
            ruleWeights = new float[activities.size()][SubActivities.size()];//ArrayList<Set> listOfActivitySets = new ArrayList<Set>(activities.size());
            for (int i=0; i< ruleWeights.length; ++i)
                for (int j =0; j<ruleWeights[i].length; j++)
                    ruleWeights[i][j] = 0;
            //int [][] subActivityWeights = new int[activities.size()][SubActivities.size()];

            //Read File Line By Line
            while ((strLine = br.readLine()) != null){ //for each sub-activity register
                // Print the content on the console
                //System.out.println (strLine);
                String[] register = strLine.split(",");
                for (int i=0; i<register.length; ++i){
                    register[i] = removeQuotes(register[i]);
                }
                if (register[2].equals("eating")) register[2] = "eatingMeal";
                if (register[2].equals("cleaning")) register[2] = "cleaningObjects";
                if (register[2].equals("placing")) register[2] = "arrangingObjects";
                if (register[2].equals("microwave")) register[2] = "microwaving";
                //if (register[4].equals("null")) register[4] = "nullSA";
                if(usingFoldIDs.contains(-1)){    // TAKE ALL REGISTRIES
                    subActivitiesRates[activities.get(register[2])][SubActivities.indexOf(register[4])] += 1;
                }
                else{ // TAKE THOSE WITH SAME FOLD -> in this case USER ID
                    if(usingFoldIDs.contains(Integer.parseInt(register[0])))
                        subActivitiesRates[activities.get(register[2])][SubActivities.indexOf(register[4])] += 1;
                }
                // Once we got the accumulated sum table, we create a rule per Activity with the form of following example:
                //(define-concept antecedent2 (w-sum (0.16 reach) (0.16 move ) (0.16 place) (0.16 open)(0.18 eat)(0.18 drink)))
                //(define-concept consequent2 (g-and User (some performsActivity medicine)))
            }
            String rule;
            // Computing normalized weights
            ArrayList<Integer> activitiesSums = new ArrayList<Integer>();
            for(int i=0; i<activities.size(); i++){
                int sum = 0;
                for(int j=0; j<SubActivities.size(); ++j){
                    if (subActivitiesRates[i][j]>0){
                        sum += subActivitiesRates[i][j];
                    }
                }
                activitiesSums.add(sum);
            }
            for(int i=0; i<activities.size(); i++){
                rule = "(define-concept antecedent"+(i+1)+" (w-sum ";
                float partialSum = 0;
                for(int j=0; j<SubActivities.size(); ++j){
                    if(subActivitiesRates[i][j] >0){
                        float weight = (float) subActivitiesRates[i][j]/activitiesSums.get(i);
                        weight = round(weight, 2);
                        ruleWeights[i][j] = weight ;//bd.floatValue();
                        partialSum += weight;
                    }
                }
                //Double check that the sum of weights for an activity is not greater than 1.0. If so, we change last value decreasing it 0.1
                if(partialSum > 1.0f){
                    float rounding = 0.01f;
                    int z=0;
                    while(z<SubActivities.size() && partialSum>1.0f){
                        if(ruleWeights[i][z] >0.0){
                            ruleWeights[i][z] -= rounding;
                            ruleWeights[i][z] = round(ruleWeights[i][z], 2);
                        }
                        z++;
                    }
                }
                for(int j=0; j<userPerformsSubActivitiesObjectAxioms.size(); ++j)
                    if(ruleWeights[i][j] > 0.0f)
                        rule += "("+ ruleWeights[i][j] +" "+userPerformsSubActivitiesObjectAxioms.get(i).get(j) +")";
                rule +="))\n(define-concept consequent"+(i+1)+ " (g-and User (some performsActivity "+ indexes.get(i)+")))\n\n";
                rules += rule;
            }
            for(int i=0; i<activities.size(); ++i){
                rules += "(define-concept Rule"+(i+1)+" (l-implies antecedent"+(i+1)+" consequent"+(i+1)+" ) )\n";
                rules += "(instance "+individualName+" Rule"+(i+1)+")\n";
            }
            // EXAMPLE OF RULE INSTANTIATION AND LINKING TO A GIVEN INDIVIDUAL
            // (define-concept Rule1 (l-implies antecedent1 consequent1 ) )
            //(instance Natalia Rule1)
            rules += "(define-concept Rules (g-or ";
            for(int i=0; i<activities.size(); ++i)
                rules+= "Rule"+(i+1)+" ";
            rules+= "))\n";
            // (define-concept Rules (g-or Rule1 Rule2 Rule3 Rule4 Rule5 Rule6 Rule7 Rule8 Rule9 Rule10))
            rules+= "(instance "+individualName+" Rules)\n";
            rules+= "\n% Is the KB satisfiable?\n(sat?)\n";
            /// Writing rules to file
            PrintWriter pw = new PrintWriter(new FileWriter(pathToWriteRules, true)); // True to append at end of file
            String completeKBAndRules = readTextFile(KBFileWithoutRules).concat(rules);
            try {
                pw.println(completeKBAndRules);
            }
            catch (Exception e){
                System.err.println("Error writing rules to file: " + e.getMessage());//oh noes!
            }
            finally{
                pw.close();
            }
            System.out.println("Final rule weights");
            printMatrix(ruleWeights);
        }
        catch (Exception e){//Catch exception if any
            System.err.println("Error in LearnWeightsAndCreateRules: " + e.getMessage());
        }
        return rules;
    }


    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_EVEN);//ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static Double round(Double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_DOWN);
        return bd.doubleValue();
    }

    /*protected static void LearnSubActivitiesInvolvedInActivities(String file){
        try{
            FileInputStream fstream = new FileInputStream(file);       //"dataset.txt"
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            String[][] listOfActivitySets = new String[activities.size()][SubActivities.size()];//ArrayList<Set> listOfActivitySets = new ArrayList<Set>(activities.size());
            for (int i=0; i< listOfActivitySets.length; ++i)
                for (int j =0; j<listOfActivitySets[i].length; j++)
                    listOfActivitySets[i][j] = "-";
            //Read File Line By Line
            while ((strLine = br.readLine()) != null){ //for each sub-activity register
                // Print the content on the console
                String[] register = strLine.split(",");
                for (int i=0; i<register.length; ++i)
                    register[i] = removeQuotes(register[i]); //TODO FIX ACCORDING TO FILE
                if (register[1].equals("eating")) register[1] = "eatingMeal";
                if (register[1].equals("cleaning")) register[1] = "cleaningObjects";
                if (register[1].equals("placing")) register[1] = "arrangingObjects";
                if (register[1].equals("microwave")) register[1] = "microwaving";
                //if(!register[3].equals("null")){
                    // We add the action to its activity set
                    int index = activities.get(register[1]);
                    //listOfActivitySets.get(index).get(activities.get(register[3]), "x");
                    listOfActivitySets[index][SubActivities.indexOf(register[3])] = "x";
                //}
            }
            printMatrix(listOfActivitySets);
            //Close the input stream
            in.close();
        }
        catch (Exception e){//Catch exception if any
            System.err.println("In LearnActionsInvolvedInActivities: Error reading dataset file: " + e.getMessage());
        }
    }*/

    static protected ActivitiesCertaintiesPairs filterActivitiesFulfillingOrderAndObjectSubSequenceConstraints(ArrayList<String> candidateActivities){
        ArrayList<String> activitiesSatisfyingAllConstraints = new ArrayList<String>();
        ArrayList<Float> activitiesCertaintiesSatisfyingAllConstraints = new ArrayList<Float>();
        try{
            ArrayList<Dictionary> activitiesOccurredInLastFTW;
            // 1. SubActivity Order SubSequence constraints
            for (int i=0; i< candidateActivities.size(); ++i){ // Activity
                activitiesOccurredInLastFTW = cutFTWInNrOfSubActFromLatestSubActivitiesForActivity(candidateActivities.get(i));
                for( int j=0; j< orderObjectsSubSequenceConstraints.get(activities.get(candidateActivities.get(i))).size(); ++j){ // TODO: optionals SAsubSequences count?
                    float ratioOfSAObjectPairsSubSequences = getDegreeOfFTWFulfillingOrderAndObjectSubSequenceConstraintPatterns(activitiesOccurredInLastFTW, orderObjectsSubSequenceConstraints.get(activities.get(candidateActivities.get(i))));
                    if(ratioOfSAObjectPairsSubSequences >= SAObjectSubSequencesRatioThreshold && !activitiesSatisfyingAllConstraints.contains(candidateActivities.get(i))){ // TODO: secondRatio filter threshold
                        activitiesSatisfyingAllConstraints.add(candidateActivities.get(i));  // TODO: weight more those with +1 pattern recognized = Add ratio for pairs SA-object matched.
                        activitiesCertaintiesSatisfyingAllConstraints.add(round(ratioOfSAObjectPairsSubSequences, 2));
                        System.out.print(candidateActivities.get(i) + " (" + round(ratioOfSAObjectPairsSubSequences, 2) + ") ");
                    }
                }
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in filterActivitiesFulfillingAllConstraints: " + e.getMessage());
        }
        return new ActivitiesCertaintiesPairs(activitiesSatisfyingAllConstraints, activitiesCertaintiesSatisfyingAllConstraints);//return candidateActivities;
    }

    static protected float getOrderAndObjectSubSequenceConstraintsRatio(String activity){
        float ratio = 0f;
        try{
            ArrayList<Dictionary> activitiesOccurredInLastFTW = cutFTWInNrOfSubActFromLatestSubActivitiesForActivity(activity);
            ratio = getDegreeOfFTWFulfillingOrderAndObjectSubSequenceConstraintPatterns(activitiesOccurredInLastFTW, orderObjectsSubSequenceConstraints.get(activities.get(activity)));
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in getOrderAndObjectSubSequenceConstraintsRatio: " + e.getMessage());
        }
        return round(ratio, 2);
    }

    static protected ArrayList<String> filterActivitiesFulfillingOrderAndObjectConstraints(ArrayList<String> candidateActivities){
        ArrayList<String> activitiesSatisfyingAllConstraints = new ArrayList<String>();
        try{
            ArrayList<Dictionary> activitiesOccurredInLastFTW;
            // 1. SubActivity Order constraints
            for (int i=0; i< candidateActivities.size(); ++i){ // Activity
                activitiesOccurredInLastFTW = cutFTWInNrOfSubActFromLatestSubActivitiesForActivity(candidateActivities.get(i));
                if(FTWFulfillsOrderAndObjectConstraintPatterns(orderObjectsConstraints.get(activities.get(candidateActivities.get(i))), activitiesOccurredInLastFTW)){
                    System.out.print(candidateActivities.get(i) + " " );
                    activitiesSatisfyingAllConstraints.add(candidateActivities.get(i));
                }   // TODO: weight more those with +1 pattern recognized = Add ratio for pairs SA-object matched.
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in filterActivitiesFulfillingAllConstraints: " + e.getMessage());
        }
        return activitiesSatisfyingAllConstraints;//return candidateActivities; // TODO: return activitiesSats..
    }

    static protected float getOrderAndObjectConstraintsCrispRatio(String activity){
        float ratio = 0f;
        try{
            ArrayList<Dictionary> activitiesOccurredInLastFTW = cutFTWInNrOfSubActFromLatestSubActivitiesForActivity(activity);
            if(FTWFulfillsOrderAndObjectConstraintPatterns(orderObjectsConstraints.get(activities.get(activity)), activitiesOccurredInLastFTW))
                ratio = 1f;
            else
                ratio = 0f;
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in getOrderAndObjectConstraintsCrispRatio: " + e.getMessage());
        }
        return ratio;
    }

    static protected float getOrderAndObjectConstraintsFuzzyRatio(String activity){
        float ratio = 0f;
        try{
            ArrayList<Dictionary> activitiesOccurredInLastFTW = cutFTWInNrOfSubActFromLatestSubActivitiesForActivity(activity);
            ratio = FTWFulfillsOrderAndObjectConstraintPatternsFuzzy(orderObjectsConstraints.get(activities.get(activity)), activitiesOccurredInLastFTW);
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in getOrderAndObjectConstraintsFuzzyRatio: " + e.getMessage());
        }
        return ratio;
    }

    static protected ActivitiesCertaintiesPairs filterActivitiesContainingSubActivityAndObjectConstraints(){
        ArrayList<String> activitiesSatisfyingAllConstraints = new ArrayList<String>();
        ArrayList<Float> certaintiesOfActivitiesSatisfyingAllConstraints = new ArrayList<Float>();
        try{
            ArrayList<Dictionary> activitiesOccurredInLastFTW;
            // 1. SubActivity Order constraints
            for (int i=0; i< orderObjectsConstraints.size(); ++i){ // Activity
                activitiesOccurredInLastFTW = cutFTWInNrOfSubActFromLatestSubActivitiesForActivity(indexes.get(i));
                //if(i==5)         System.out.println("FTW size for "+indexes.get(i)+activitiesOccurredInLastFTW.size());
                float ratioOfSAobjectPairs = FTWFulfillsSubActivityAndObjectConstraintPatterns(orderObjectsConstraints.get(i), activitiesOccurredInLastFTW);
                //System.out.println("Ratio of SA-objects pairs for activity "+indexes.get(i)+" "+ratioOfSAobjectPairs);
                if(ratioOfSAobjectPairs >= fastPreselectionRatioThreshold && !activitiesSatisfyingAllConstraints.contains(indexes.get(i))){// TODO: Check repetitions of given patterns to give more weight on credibility of that activity being happening
                    System.out.print(indexes.get(i) + " (" + round(ratioOfSAobjectPairs,2) + ") ");
                    activitiesSatisfyingAllConstraints.add(indexes.get(i));
                    certaintiesOfActivitiesSatisfyingAllConstraints.add(round(ratioOfSAobjectPairs,2));
                }
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in filterActivitiesContainingSubActivityAndObjectConstraints: " + e.getMessage());
        }
        //System.out.println("Size of activities fulfillingAllConstraints: "+ activitiesSatisfyingAllConstraints.size());
        return new ActivitiesCertaintiesPairs(activitiesSatisfyingAllConstraints, certaintiesOfActivitiesSatisfyingAllConstraints);
    }

    /*static protected float getMinDifferentObjCategoriesRequiredRatio(String activity){ // TODO: To do this we need to count only objects, and many activities not belonging to the same SA could be accounted, so it is difficult. If we count same SA, we are obtaining the already computed preRatio
        float ratioOfObjCategories= 0;
        try{
            ArrayList<Dictionary> activitiesOccurredInLastFTW = cutFTWInNrOfSubActFromLatestSubActivitiesForActivity(activity);
            ratioOfObjCategories = FTWFulfillsMinObjCategoriesWithDegr(objectCategoriesConstraints.get(activities.get(activity)), activitiesOccurredInLastFTW);
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in getMinDifferentObjCategoriesRequiredRatio: " + e.getMessage());
        }
        return round(ratioOfObjCategories,2);
    }*/

    /*static protected float FTWFulfillsMinObjCategoriesWithDegr(ArrayList<ArrayList<String>> objectCategories, ArrayList<Dictionary> activitiesOccurredInLastFTW){
        boolean exit = false;
        boolean found;
        boolean fulfilled;
        String SA = "";
        float count=0;
        try{
            for(int i=0; i< objectCategories.size(); ++i){
                found = false;
                for(int j = 0; j<activitiesOccurredInLastFTW.size() && !found; ++j){
                    if(activitiesOccurredInLastFTW.get(j).get("subActivity").equals(SA)){
                        fulfilled = false;
                        for(int k=0; k < ((ArrayList<String>) activitiesOccurredInLastFTW.get(j).get("objects")).size() && !fulfilled ; ++k){
                            //if(subActivitySequence.get(i).get(SA).contains(((ArrayList<String>) activitiesOccurredInLastFTW.get(j).get("objects")).get(k)) || objectBelongsToCategory(((ArrayList<String>) activitiesOccurredInLastFTW.get(j).get("objects")).get(k), subActivitySequence.get(i).get(SA))){
                            if(objectsAppearOrBelongToCategories(((ArrayList<String>) activitiesOccurredInLastFTW.get(j).get("objects")), objectCategories.get(i).get(SA))){
                                found = true;   // AS HERE WE DONT SEE ORDER; JUST OCCURRENCE; WE DELETE FROM ACTIVITIES OCCURRED IN LAST FTW SO THAT WE CAN COUNT REPETITIONS OF SA WITHIN AN ACTIVITY
                                count = count+1;
                                fulfilled = true;
                            }
                        }
                    }
                }
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in FTWFulfillsMinObjCategoriesWithDegr: " + e.getMessage());
        }
        return count/((float)objectCategories.size());
    }*/

    static protected float getActivityPreRatioForContainedSubActivityAndObjectConstraints(String activity){
        float ratioOfSAobjectPairs= 0;
        try{
            ArrayList<Dictionary> activitiesOccurredInLastFTW = cutFTWInNrOfSubActFromLatestSubActivitiesForActivity(activity);
            ratioOfSAobjectPairs = FTWFulfillsSubActivityAndObjectConstraintPatterns(orderObjectsConstraints.get(activities.get(activity)), activitiesOccurredInLastFTW);
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in getActivityPreRatioForContainedSubActivityAndObjectConstraints: " + e.getMessage());
        }
        return round(ratioOfSAobjectPairs,2);
    }

    static protected  boolean FTWFulfillsOrderConstraintPatterns(ArrayList<String> subActivitySequence, ArrayList<Dictionary> activitiesOccurredInLastFTW){
        boolean found = false;
        int sequenceSize = subActivitySequence.size()-1;//2
        try{
            boolean bingo = false;
            boolean exit = false;
            int i= activitiesOccurredInLastFTW.size()-1;
            // CHECK SUBACTIVITY ORDER CONSTRAINTS BACKWARDS, TO STOP SEARCHING IF CONDITION DOES NOT APPEAR
            while (i >=subActivitySequence.size()  && !found){ // SubActivities in a pattern
                exit = false;
                bingo = false;
                for(int j=subActivitySequence.size()-1; j>=0 && !exit; j--){
                    if(activitiesOccurredInLastFTW.get(i-(sequenceSize-j)).get("subActivity").equals(subActivitySequence.get(j)))
                        bingo = true;
                    else{
                        exit = true;
                        bingo = false;
                    }
                }
                if(!exit & bingo)
                    found = true;
                i--;
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in FTWFulfillsOrderConstraintPatterns: " + e.getMessage());
        }
        return found; // = Pattern not found
    }

    static protected float getDegreeOfFTWFulfillingOrderAndObjectSubSequenceConstraintPatterns(ArrayList<Dictionary> activitiesOccurredInLastFTW, ArrayList<SubActivitiesSubSequence> subActivitiesSubSequences){
        boolean exit = false;
        boolean found;
        boolean fulfilled;
        String SA = "";
        float count=0;
        int totalSubActivitiesSubSequences = 0;
        try{
            for(int i=0; i< subActivitiesSubSequences.size(); ++i){ // SUBSEQUENCE
                int subSequenceSize = subActivitiesSubSequences.get(i).getSubActivities().size();
                totalSubActivitiesSubSequences += (subSequenceSize* subActivitiesSubSequences.get(i).getRequiresMinReps()) ;
                for(int rep =0; rep< subActivitiesSubSequences.get(i).getRequiresMinReps(); ++rep){ // MIN NR OF REPETITIONS
                    for(int sa=0; sa<subSequenceSize; ++sa){ //SUB-ACTIVITY IN EACH SUBSEQUENCE
                        found = false;
                        Set<String> subAct = subActivitiesSubSequences.get(i).getSubActivities().get(sa).keySet();
                        Iterator iter = subAct.iterator();
                        while (iter.hasNext()) {
                            SA =(String)iter.next();
                        }
                        for(int j = 0; j<activitiesOccurredInLastFTW.size() && !found; ++j){ //SUB-ACTIVITY IN THE FTW TO COMPARE WITH
                            if(activitiesOccurredInLastFTW.get(j).get("subActivity").equals(SA)){
                                fulfilled = false;
                                for(int k=0; k < ((ArrayList<String>) activitiesOccurredInLastFTW.get(j).get("objects")).size() && !fulfilled ; ++k){ //OBJECT IN EACH SUB-ACTIVITY
                                    //if(subActivitySequence.get(i).get(SA).contains(((ArrayList<String>) activitiesOccurredInLastFTW.get(j).get("objects")).get(k)) || objectBelongsToCategory(((ArrayList<String>) activitiesOccurredInLastFTW.get(j).get("objects")).get(k), subActivitySequence.get(i).get(SA))){
                                    if(objectsAppearOrBelongToCategories(((ArrayList<String>) activitiesOccurredInLastFTW.get(j).get("objects")), subActivitiesSubSequences.get(i).getSubActivities().get(sa).get(SA))){
                                        found = true;   // AS HERE WE DONT SEE ORDER; JUST OCCURRENCE; WE DELETE FROM ACTIVITIES OCCURRED IN LAST FTW SO THAT WE CAN COUNT REPETITIONS OF SA WITHIN AN ACTIVITY
                                        count = count+1;
                                        fulfilled = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }                        //return true;
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in FTWFulfillsOrderAndObjectSubSequenceConstraintPatterns: " + e.getMessage());
        }
        return count/((float)totalSubActivitiesSubSequences);//totalConditionsToFulfill;//return exit; // = Pattern not found
    }

    static protected float FTWFulfillsSubActivityAndObjectConstraintPatterns(ArrayList<HashMap<String, ArrayList<String>>> subActivitySequence, ArrayList<Dictionary> activitiesOccurredInLastFTW){
        boolean exit = false;
        boolean found;
        boolean fulfilled;
        String SA = "";
        float count=0;
        try{
            for(int i=0; i< subActivitySequence.size(); ++i){
                found = false;
                Set<String> subAct = subActivitySequence.get(i).keySet();
                Iterator iter = subAct.iterator();
                while (iter.hasNext()) {
                    SA =(String)iter.next();
                }
                for(int j = 0; j<activitiesOccurredInLastFTW.size() && !found; ++j){
                    if(activitiesOccurredInLastFTW.get(j).get("subActivity").equals(SA)){
                        fulfilled = false;
                        for(int k=0; k < ((ArrayList<String>) activitiesOccurredInLastFTW.get(j).get("objects")).size() && !fulfilled ; ++k){
                            //if(subActivitySequence.get(i).get(SA).contains(((ArrayList<String>) activitiesOccurredInLastFTW.get(j).get("objects")).get(k)) || objectBelongsToCategory(((ArrayList<String>) activitiesOccurredInLastFTW.get(j).get("objects")).get(k), subActivitySequence.get(i).get(SA))){
                            if(objectsAppearOrBelongToCategories(((ArrayList<String>) activitiesOccurredInLastFTW.get(j).get("objects")), subActivitySequence.get(i).get(SA))){
                                found = true;   // AS HERE WE DONT SEE ORDER; JUST OCCURRENCE; WE DELETE FROM ACTIVITIES OCCURRED IN LAST FTW SO THAT WE CAN COUNT REPETITIONS OF SA WITHIN AN ACTIVITY
                                count = count +1;
                                fulfilled = true;
                            }
                        }
                        //if(found)
                          //  activitiesOccurredInLastFTW.remove(j);
                    }
                }
            }                        //return true;
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in FTWFulfillsSubActivityAndObjectConstraintPatterns: " + e.getMessage());
        }
        return count/((float)subActivitySequence.size());//totalConditionsToFulfill;//return exit; // = Pattern not found
    }

    // same as objectsBelongsToCategories but more permissive (enough with one of the objects and SA to appear to add a partial weight)
    static protected boolean objectsAppearOrBelongToCategories(ArrayList<String> objects, ArrayList<String> objectsAndCategories){
        boolean continueSearching = true;
        try{
            if(!objectsAndCategories.containsAll(objects)){
                for(int i=0; i<objects.size() && continueSearching; i++)
                    if(objectsAndCategories.contains(objects.get(i)))
                        return true;//continueSearching = false;
                    else
                        if ( objectBelongsToCategories(objects.get(i), objectsAndCategories))
                            return true;
                        else return false;
            }
            else return true;
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in objectsAppearOrBelongToCategories: " + e.getMessage());
        }
        return false;
    }

    static protected  boolean FTWFulfillsOrderAndObjectConstraintPatterns(ArrayList<HashMap<String, ArrayList<String>>> subActivitySequence, ArrayList<Dictionary> activitiesOccurredInLastFTW){
        boolean found = false;
        int sequenceSize = subActivitySequence.size()-1;
        String subActivity = "";
        boolean continueMatching = true;
        try{
            int i= activitiesOccurredInLastFTW.size();//-1;
            // CHECK SUBACTIVITY ORDER CONSTRAINTS BACKWARDS, TO STOP SEARCHING IF CONDITION DOES NOT APPEAR
            while (i >=subActivitySequence.size()  && !found){ // SubActivities in a pattern
                continueMatching = true;
                for(int j=subActivitySequence.size()-1; j>=0 && continueMatching; j--){
                    Set<String> subAct = subActivitySequence.get(j).keySet();
                    Iterator iter = subAct.iterator();
                    while (iter.hasNext()) {
                        subActivity =(String)iter.next();
                    }
                    if(subActivity.equals(activitiesOccurredInLastFTW.get(i -1- (sequenceSize - j)).get("subActivity"))){//
                        if(!subActivity.equals("null")){ // TODO NULL_SA DOESNT WORK? Check object requirements for all except Null SubActivity (as it does not have any specific object associated?)//TODO: CAN BE ASSOCIATED THOSE FROM THE ACTIVITY
                            if(!categoriesRepresentedInObjects(subActivitySequence.get(j).get(subActivity), (ArrayList<String>) activitiesOccurredInLastFTW.get(i - 1 - (sequenceSize - j)).get("objects"))){
                                continueMatching = false;
                            }
                            else
                                continueMatching = true;
                        }
                        else continueMatching= true;
                    }
                    else continueMatching = false;
                }
                if(continueMatching)
                    found = true;
                i--;
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in FTWFulfillsOrderAndObjectConstraintPatterns: " + e.getMessage());
        }
        return found; // = Pattern not found
    }

    static protected  float FTWFulfillsOrderAndObjectConstraintPatternsFuzzy(ArrayList<HashMap<String, ArrayList<String>>> subActivitySequence, ArrayList<Dictionary> activitiesOccurredInLastFTW){
        boolean found = false;
        int sequenceSize = subActivitySequence.size()-1;
        String subActivity = "";
        int fullfilledOrderObjectConstraints = 0;
        boolean continueMatching = true;
        try{
            int i= activitiesOccurredInLastFTW.size();//-1;
            // CHECK SUBACTIVITY ORDER CONSTRAINTS BACKWARDS, TO STOP SEARCHING IF CONDITION DOES NOT APPEAR
            while (i >=subActivitySequence.size()  && !found){ // SubActivities in a pattern
                continueMatching = true;
                for(int j=subActivitySequence.size()-1; j>=0 && continueMatching; j--){
                    Set<String> subAct = subActivitySequence.get(j).keySet();
                    Iterator iter = subAct.iterator();
                    while (iter.hasNext()) {
                        subActivity =(String)iter.next();
                    }
                    if(subActivity.equals(activitiesOccurredInLastFTW.get(i -1- (sequenceSize - j)).get("subActivity"))){//
                        //if(!subActivity.equals("null")){ // TODO NULL_SA DOESNT WORK? Check object requirements for all except Null SubActivity (as it does not have any specific object associated?)//TODO: CAN BE ASSOCIATED THOSE FROM THE ACTIVITY
                            if(!categoriesRepresentedInObjects(subActivitySequence.get(j).get(subActivity), (ArrayList<String>) activitiesOccurredInLastFTW.get(i - 1 - (sequenceSize - j)).get("objects"))){
                                continueMatching = false;
                            }
                            else{
                                continueMatching = true;
                                fullfilledOrderObjectConstraints++;
                            }
                        //}
                        //else continueMatching= true;
                    }
                    else continueMatching = false;
                }
                if(continueMatching)
                    found = true;
                i--;
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in FTWFulfillsOrderAndObjectConstraintPatterns: " + e.getMessage());
        }
        return round(((float)fullfilledOrderObjectConstraints/subActivitySequence.size()), 2); // = Pattern not found
    }

    static protected ArrayList<String> FTWFulfillsObjectConstraintPatterns(ArrayList<String> requiredObjects, ArrayList<Dictionary> activitiesOccurredInLastFTW){
        boolean found = false;
        boolean bingo = false;
        boolean exit = false;
        String activity = "";
        ArrayList<String> filteredActivities = new ArrayList<String>();
        try{
            int i= 0;
            while (i <= (activitiesOccurredInLastFTW.size()-requiredObjects.size()) && !found){ // SubActivities in a pattern  //TODO: Try find it backwards to save time?
                exit = false;
                bingo = false;
                for(int j=0; j<requiredObjects.size() && !exit; ++j){ // at least one object or all //TODO: associate objects-subActivities. Empty Kb, Insert latest ones and query
                    if(((ArrayList<String>)activitiesOccurredInLastFTW.get(i+j).get("objects")).contains(requiredObjects.get(j))){
                        bingo = true;
                        activity = indexes.get(j);
                    }
                    else{
                        exit = true;
                        bingo = false;
                    }
                }
                if(!exit && bingo){
                    found = true;
                    filteredActivities.add(activity);
                }
                i++;
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in FTWFulfillsObjectConstraintPatterns: " + e.getMessage());
        }
        return filteredActivities;// found // = Object not found
    }

    /*static protected String getActivitiesFulfillingExtraConstraintsInFTW(ArrayList<Dictionary> activitiesOccurredInLastFTW, ArrayList<String> probableActivities){
        // TODO Checks for and objects relative positions, spacedOrder.   // TODO: reuse calculation of FTW
        //ArrayList<String> fulfillingActivities = probableActivities;
        String activity = "";
        try{
            boolean constraintFound = false;
            boolean bingo = false;
            boolean exit = false;
            float[] pos1; // Positions of objects hold with left and right hands
            float[] pos2;
            int i= 0;
            String object1, object2; // objects held with each hand
            String [] correctHandPos;
            if(probableActivities.contains(STACKING) || probableActivities.contains(UNSTACKING)){
                while (i < (activitiesOccurredInLastFTW.size()-1) && !constraintFound){ // SubActivities in a pattern
                    exit = false;
                    bingo = false;
                    for(int h=0; h<((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objects")).size()-1 && !exit; ++h){
                        object1 = ((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objects")).get(h);
                        object2 = ((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objects")).get(h+1); // TODO: check same type not only stackable? trunk to int for efficiency
                        if(objectBelongsToCategory(object1, STACKABLE) && objectBelongsToCategory(object2, STACKABLE)){
                            pos1 = new float[]{((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h).get(0), ((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h).get(1), ((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h).get(2)};
                            pos2 = new float[]{((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h+1).get(0), ((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h+1).get(1), ((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h+1).get(2)};
                            if(objectsOnTopOfEachOther(pos1, pos2)){ //TODO:define with SWRL rules?/MAMDANI?
                                bingo = true;
                                exit = true;
                                //fulfillingActivities.clear();
                                //fulfillingActivities.add(STACKING);
                                activity = STACKING;
                                System.out.println(" DETECTED STACKING  _____________________");
                            }
                            else{
                                if(objectsOnSameSurface(pos1, pos2)){
                                    bingo = true;
                                    exit = true;
                                    //fulfillingActivities.clear();
                                    //fulfillingActivities.add(UNSTACKING);
                                    activity = UNSTACKING;
                                    System.out.println(" DETECTED UNSTACKING -----------------------------");
                                }
                            }
                        }
                    }
                    if(!exit && bingo)
                        constraintFound = true;
                    i++;
                }
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in FTWFulfillsExtraConstraints: " + e.getMessage());
        }
        return activity;//fulfillingActivities;
    }*/

    static protected ArrayList<String> filterActivitiesFulfillingExtraConstraints( ArrayList<String> candidateActivities){
        // TODO Checks for and objects relative positions, spacedOrder.   // TODO: reuse calculation of FTW
        ArrayList<String> filteredActivities = new ArrayList<String>();// = candidateActivities;
        ArrayList<String> filteredActivitiesStackingObjects = new ArrayList<String>();// = candidateActivities;
        ArrayList<String> filteredActivitiesUnstackingObjects = new ArrayList<String>();// = candidateActivities;
        String state ="";
        return candidateActivities;}
        /*try{
            boolean exit = false;
            boolean found = false;
            float[] pos1; // Positions of objects hold with left and right hands
            float[] pos2;
            int i;
            String object1, object2; // objects held with each hand
            String [] correctHandPos;
            ArrayList<Dictionary> activitiesOccurredInLastFTW;
            int nStackingMovements = 0;
            int nUnstackingMovements = 0;
            String finalState = "";
            if(candidateActivities.contains(STACKING) || candidateActivities.contains(UNSTACKING)){
                for(int j=0; j<candidateActivities.size(); ++j){   // && !found?
                    nStackingMovements =0;
                    nUnstackingMovements = 0;
                    activitiesOccurredInLastFTW = cutFTWInNrOfSubActFromLatestSubActivitiesForActivity(candidateActivities.get(j));
                    i= 0;
                    exit = false;
                    state = "";
                    while (i < (activitiesOccurredInLastFTW.size()-1) ){// && !exit){ // SubActivities in a pattern
                        for(int h=0; h<((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objects")).size()-1 && !exit; ++h){
                            object1 = ((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objects")).get(h);
                            object2 = ((ArrayList<String>)activitiesOccurredInLastFTW.get(i).get("objects")).get(h+1); // TODO: check same type not only stackable? trunk to int for efficiency
                            if(objectBelongsToCategory(object1, STACKABLE) && objectBelongsToCategory(object2, STACKABLE) && object1.equals(object2)){
                                pos1 = new float[]{((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h).get(0), ((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h).get(1), ((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h).get(2)};
                                pos2 = new float[]{((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h+1).get(0), ((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h+1).get(1), ((ArrayList<ArrayList<Float>>)activitiesOccurredInLastFTW.get(i).get("objectsPositions")).get(h+1).get(2)};
                                if(objectsOnTopOfEachOther(pos1, pos2) && state.equals("")) //TODO:define with SWRL rules?/MAMDANI?
                                    state = "stacked";
                                else{
                                    if(objectsOnTopOfEachOther(pos1, pos2) && state.equals("unstacked")){
                                        System.out.println("StackingMov detected in activ "+candidateActivities.get(j));
                                        nStackingMovements++;
                                        if((nStackingMovements %2) == 0){
                                            System.out.println("STACKINGMOV DETECTED IN ACTIV "+candidateActivities.get(j));
                                            //if(!filteredActivitiesStackingObjects.contains(candidateActivities.get(j)))
                                                filteredActivitiesStackingObjects.add(candidateActivities.get(j));
                                            //nStackingMovements =0;
                                            finalState = "stacked"; // here?
                                        }
                                        state = "stacked";
                                        //exit = true;
                                    }
                                    else{
                                        if(objectsOnSameSurface(pos1, pos2) && state.equals("")){
                                            state = "unstacked";
                                        }
                                        else{
                                            if(objectsOnSameSurface(pos1, pos2) && state.equals("stacked")){
                                                System.out.println("UnstackingMov detected in activ "+candidateActivities.get(j));
                                                nUnstackingMovements++;
                                                if((nUnstackingMovements %2) ==0){//TODO:3?
                                                    System.out.println("UNSTACKINGMOV DETECTED IN ACTIV "+candidateActivities.get(j));
                                                    //if(!filteredActivitiesUnstackingObjects.contains(candidateActivities.get(j)))
                                                        filteredActivitiesUnstackingObjects.add(candidateActivities.get(j));
                                                    //nUnstackingMovements =0;
                                                    finalState = "unstacked";
                                                }
                                                state = "unstacked";
                                                //exit = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        i++;
                    }
                    //if(exit)
                      //  found = true;
                }
                System.out.println("\n"+nStackingMovements+" events. Filtered StackingObj activ: ");
                for(int u=0; u<filteredActivitiesStackingObjects.size(); ++u)
                    System.out.print(filteredActivitiesStackingObjects.get(u)+" ");
                System.out.println("\n"+nUnstackingMovements+" events. Filtered UnStackingObj activ: ");
                for(int u=0; u<filteredActivitiesUnstackingObjects.size(); ++u)
                    System.out.print(filteredActivitiesUnstackingObjects.get(u)+" ");
                if(filteredActivitiesStackingObjects.size() <1 && filteredActivitiesUnstackingObjects.size() <1){
                    /*if(candidateActivities.contains(STACKING)){ // QUITAR O NO? -> dejar a fDL que lo decida cuando se arrastra incertidumbre de un filtro a otro. Final evidence is decided by uncertainty reasoner.
                        //filteredActivitiesStackingObjects = removeDuplicates(filteredActivitiesStackingObjects);
                        candidateActivities.remove(STACKING);
                    }
                    if(candidateActivities.contains(UNSTACKING)){
                        //filteredActivitiesStackingObjects = removeDuplicates(filteredActivitiesStackingObjects);
                        candidateActivities.remove(UNSTACKING);
                    }*/
/*                    return candidateActivities;
                }
                if(filteredActivitiesStackingObjects.contains(STACKING)){
                    if(nOfMovements(STACKING, filteredActivitiesStackingObjects)> nOfMovements(UNSTACKING, filteredActivitiesUnstackingObjects)){
                        filteredActivitiesStackingObjects = removeDuplicates(filteredActivitiesStackingObjects);
                        return filteredActivitiesStackingObjects;
                    }
                    else{
                        if(nOfMovements(STACKING, filteredActivitiesStackingObjects)< nOfMovements(UNSTACKING, filteredActivitiesUnstackingObjects) && filteredActivitiesUnstackingObjects.contains(UNSTACKING)){
                            filteredActivitiesUnstackingObjects = removeDuplicates(filteredActivitiesUnstackingObjects);
                            return filteredActivitiesUnstackingObjects;
                        }else{  // Return both sub-sets removing duplicates
                            filteredActivities.addAll(filteredActivitiesStackingObjects);
                            for(int k=0; k<filteredActivitiesUnstackingObjects.size(); ++k)
                                if(!filteredActivities.contains(filteredActivitiesUnstackingObjects.get(k)))
                                    filteredActivities.add(filteredActivitiesUnstackingObjects.get(k)); // Add if not contains, or add all and then return highest count
                            filteredActivities = removeDuplicates(filteredActivities);
                            return filteredActivities;
                        }
                    }
                }else{
                    if(filteredActivitiesUnstackingObjects.contains(UNSTACKING)){
                        if(nOfMovements(STACKING, filteredActivitiesStackingObjects)< nOfMovements(UNSTACKING, filteredActivitiesUnstackingObjects)){
                            filteredActivitiesUnstackingObjects = removeDuplicates(filteredActivitiesUnstackingObjects);
                            return filteredActivitiesUnstackingObjects; // removedups?
                        }else{
                            if(nOfMovements(STACKING, filteredActivitiesStackingObjects)> nOfMovements(UNSTACKING, filteredActivitiesUnstackingObjects) && filteredActivitiesStackingObjects.contains(STACKING)){
                                filteredActivitiesStackingObjects = removeDuplicates(filteredActivitiesStackingObjects);
                                return filteredActivitiesStackingObjects;
                            }
                            else{ // Return both sub-sets removing duplicates
                                filteredActivities.addAll(filteredActivitiesStackingObjects);
                                for(int k=0; k<filteredActivitiesUnstackingObjects.size(); ++k)
                                    if(!filteredActivities.contains(filteredActivitiesUnstackingObjects.get(k)))
                                        filteredActivities.add(filteredActivitiesUnstackingObjects.get(k)); // Add if not contains, or add all and then return highest count
                                filteredActivities = removeDuplicates(filteredActivities);
                                return filteredActivities;
                            }
                        }
                    }

                }
    */
                /*if((filteredActivitiesStackingObjects.contains(STACKING) && filteredActivitiesStackingObjects.contains(UNSTACKING)) && (filteredActivitiesUnstackingObjects.contains(STACKING) && filteredActivitiesUnstackingObjects.contains(UNSTACKING))){
                    if(filteredActivitiesStackingObjects.size()> filteredActivitiesUnstackingObjects.size()) //
                        return filteredActivitiesStackingObjects;
                    else{
                        if(filteredActivitiesUnstackingObjects.size()> filteredActivitiesStackingObjects.size())
                            return filteredActivitiesUnstackingObjects;
                        else{
                            // Return both sub-sets removing duplicates
                            filteredActivities.addAll(filteredActivitiesStackingObjects);
                            for(int k=0; k<filteredActivitiesUnstackingObjects.size(); ++k)
                                if(!filteredActivities.contains(filteredActivitiesUnstackingObjects.get(k)))
                                    filteredActivities.add(filteredActivitiesUnstackingObjects.get(k)); // Add if not contains, or add all and then return highest count
                            return filteredActivities;
                        }
                    }
                }
                else{
                    if((filteredActivitiesStackingObjects.contains(STACKING) || filteredActivitiesStackingObjects.contains(UNSTACKING)) && (filteredActivitiesUnstackingObjects.contains(STACKING) || filteredActivitiesUnstackingObjects.contains(UNSTACKING))){
                        if(filteredActivitiesStackingObjects.size()> filteredActivitiesUnstackingObjects.size()){ // largest size or largest count of stack/unstack in its respective array
                            return filteredActivitiesStackingObjects;
                        }
                        else{
                            if(filteredActivitiesUnstackingObjects.size()> filteredActivitiesStackingObjects.size())
                                return filteredActivitiesUnstackingObjects;
                            else{ // Return both sets removing duplicates
                                filteredActivities.addAll(filteredActivitiesStackingObjects);
                                for(int k=0; k<filteredActivitiesUnstackingObjects.size(); ++k)
                                    if(!filteredActivities.contains(filteredActivitiesUnstackingObjects.get(k)))
                                        filteredActivities.add(filteredActivitiesUnstackingObjects.get(k));
                                return filteredActivities;
                            }
                        }
                    }
                    else{
                        if(filteredActivitiesStackingObjects.contains(STACKING) || filteredActivitiesStackingObjects.contains(UNSTACKING))
                            return filteredActivitiesStackingObjects;
                        else
                            if(filteredActivitiesUnstackingObjects.contains(STACKING) || filteredActivitiesUnstackingObjects.contains(UNSTACKING))
                                return filteredActivitiesUnstackingObjects;
                            else{
                                /*if(candidateActivities.contains(STACKING))
                                    candidateActivities.remove(STACKING);
                                if(candidateActivities.contains(UNSTACKING))
                                    candidateActivities.remove(UNSTACKING);*/
                                /*return candidateActivities;
                            }
                    }*/

/*            }
            else{
                return candidateActivities;
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in filterActivitiesFulfillingExtraConstraints: " + e.getMessage());
        }
        return filteredActivities;
    }*/

    static protected ArrayList<String> removeDuplicates(ArrayList<String> elements){
        ArrayList<String> cleanArray = new ArrayList<String>();
        for(int i=0; i< elements.size(); ++i){
            if(!cleanArray.contains(elements.get(i)))
                cleanArray.add(elements.get(i));
        }
        return cleanArray;
    }

    static protected int nOfMovements(String movementType, ArrayList<String> movements){
        int sum = 0;
        for(int i=0; i< movements.size(); ++i){
            if(movements.get(i).equals(movementType))
                sum++;
        }
        return sum;
    }

    /*static protected ArrayList<Dictionary> cutFTWInFramesFromLatestSubActivitiesForActivity(String activity){
        ArrayList<Dictionary> subActivitiesOccurredInFTW = new ArrayList<Dictionary>();
        try{
            boolean maxTimeExceeded = false; // 80 extra buffer frames
            int oldestFrameSubActivityToRetrieve = Math.round(currentFrame-(activitiesDurationsMean_Stdev.get(activities.get(activity)).get(2))) -80; // CurrentFrame -Max duration of activity
            if (oldestFrameSubActivityToRetrieve <0)
                oldestFrameSubActivityToRetrieve = 0;
            int i= KBmirrorSubActivities.size()-1;
            while(i>=0 && !maxTimeExceeded){
                if(((Integer)KBmirrorSubActivities.get(i).get("startFrame")) < oldestFrameSubActivityToRetrieve) // TODO: endFrame?
                    maxTimeExceeded = true;
                else{
                    subActivitiesOccurredInFTW.add(0, KBmirrorSubActivities.get(i)); //TODO: multiply certainty by degree of belonging to the time sliding window. Useful when not updating KB?
                }
                i--;
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in cutFTWInFramesFromLatestSubActivitiesForActivity: " + e.getMessage());
        }
        return subActivitiesOccurredInFTW;
    }*/

    static protected ArrayList<Dictionary> cutFTWInNrOfSubActFromLatestSubActivitiesForActivity(String activity){
        ArrayList<Dictionary> subActivitiesOccurredInFTW = new ArrayList<Dictionary>();
        try{
            boolean maxNrOfSubActsReached = false;
            int maxNrOfSubActs = (timeWindowSizeRatio * activitiesDurationsInNrOfSubActivities.get(activities.get(activity)));
            int i= KBmirrorSubActivities.size()-1;
            while(i>=0 && !maxNrOfSubActsReached){
                if(subActivitiesOccurredInFTW.size() >= maxNrOfSubActs)
                    maxNrOfSubActsReached = true;
                else{
                    subActivitiesOccurredInFTW.add(0, KBmirrorSubActivities.get(i)); //TODO: multiply certainty by degree of belonging to the time sliding window. Useful when not updating KB?
                }
                i--;
            }
            while(KBmirrorSubActivities.size()> 40){
                KBmirrorSubActivities.remove(0);
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in cutFTWInNrOfSubActFromLatestSubActivitiesForActivity: " + e.getMessage());
        }
        return subActivitiesOccurredInFTW;
    }

    //Method used for detecting activity Stacking
    static protected boolean objectsOnTopOfEachOther(float[] pos1, float[] pos2){
        try{// distancia vertical (objeto1, objeto2) < tanto   entonces accion es apilar(objeto1, objeto2).
            //if (euclideanDistance3D(pos1, pos2) < objectObjectDistance3DThreshold && verticalDistance(pos1, pos2) < verticalDistanceThreshold && horizontalZDistance(pos1, pos2)< sameVerticalPlaneThreshold && horizontalXDistance(pos1, pos2)< sameVerticalPlaneThreshold) // in cm
            if (sameX(pos1, pos2)){
                //System.out.println("1-sameX!");
                if(sameZ(pos1, pos2)){
                   // System.out.println("2-SameZ!");
                    if(nearAlongYAxis(pos1, pos2)){
                     //   System.out.println("3-NearAlongYAxis and ON TOP!");
                        return true;
                    }
                }
            }
            /*if (sameX(pos1, pos2) && sameZ(pos1, pos2) && nearAlongYAxis(pos1, pos2)){
                //System.out.println("On top!");
                return true; // TODO: insert object1-isOnTopOf-object2
            }*/
            else return false;
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in objectsOnTopOfEachOther: " + e.getMessage());
        }
        return false;
    }
    //Method used for detecting activity Unstacking
    static protected boolean objectsOnSameSurface(float[] pos1, float[] pos2){
        try{
            // distancia vertical (objeto1, objeto2) < tanto   entonces accion es apilar(objeto1, objeto2).
            //if (euclideanDistance3D(pos1, pos2) < objectObjectDistance3DThreshold && horizontalXDistance(pos1, pos2)< horizontalDistanceThreshold && horizontalZDistance(pos1, pos2) < horizontalDistanceThreshold && verticalDistance(pos1, pos2)< sameHorizontalPlaneThreshold) // TODO: Fuzzify verticallyClose, horizontallyClose
            if (sameY(pos1, pos2)){
                //System.out.println("1-SameY!");
                if(nearAlongXAxis(pos1, pos2)){
                   // System.out.println("2-NearAlongXAxis!");
                    if(nearAlongZAxis(pos1, pos2)){
                     //   System.out.println("3-NearAlongZAxis and on SAME SURFACE!");
                        return true;
                    }
                }
            }
            //}if(sameY(pos1, pos2) && nearAlongXAxis(pos1, pos2) && nearAlongZAxis(pos1, pos2)){
                //System.out.println("On surface!");
                //return true;
            else return false;
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in objectsOnSameSurface: " + e.getMessage());
        }
        return false;
    }

    static protected boolean nearAlongXAxis(float[] pos1, float[] pos2){
        if (Math.abs(pos1[0]-pos2[0]) < nearAlongXAxisThreshold)
            return true;
        else return false;
    }
    /*static protected boolean nearAlongYAxis(float[] pos1, float[] pos2){
        if (Math.abs(pos1[1]-pos2[1]) < touchingAlongYAxisThreshold)
            return true;
        else return false;
    }
    static protected boolean nearAlongZAxis(float[] pos1, float[] pos2){
        if (Math.abs(pos1[2]-pos2[2]) < nearAlongZAxisThreshold)
            return true;
        else return false;
    }*/
    static protected boolean nearAlongYAxis(float[] pos1, float[] pos2){
        if (Math.abs(pos1[2]-pos2[2]) < touchingAlongYAxisThreshold)
            return true;
        else return false;
    }
    static protected boolean nearAlongZAxis(float[] pos1, float[] pos2){
        if (Math.abs(pos1[1]-pos2[1]) < nearAlongZAxisThreshold)
            return true;
        else return false;
    }

    static protected boolean sameX(float[] pos1, float[] pos2){
        // distancia vertical (objeto1, objeto2) < tanto   entonces accion es apilar(objeto1, objeto2).
        if (Math.abs(pos1[0]-pos2[0]) < sameXThreshold)
            return true;
        else return false;
    }

    static protected boolean sameY(float[] pos1, float[] pos2){
        if (Math.abs(pos1[2]-pos2[2]) < sameYThreshold)
            return true;
        else return false;
    }
    static protected boolean sameZ(float[] pos1, float[] pos2){
        if (Math.abs(pos1[1]-pos2[1]) < sameZThreshold)
            return true;
        else return false;
    }

    /*static protected boolean sameY(float[] pos1, float[] pos2){
        if (Math.abs(pos1[1]-pos2[1]) < sameYThreshold)
            return true;
        else return false;
    }
    static protected boolean sameZ(float[] pos1, float[] pos2){
        if (Math.abs(pos1[2]-pos2[2]) < sameZThreshold)
            return true;
        else return false;
    }*/

    /*static protected boolean objectsBelongToCategories(ArrayList<String> objects, ArrayList<String> objectCategories){// TODO: alternative to ontology missing query
        boolean continueSearching = true;
        try{
            if(!objectCategories.containsAll(objects))
                for(int i=0; i<objects.size() && continueSearching; i++)
                    if(!objectCategories.contains(objects.get(i)) && !objectBelongsToCategories(objects.get(i), objectCategories))
                        continueSearching = false;
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in objectsBelongToCategories: " + e.getMessage());
        }
        return continueSearching;
    }*/

    static protected boolean categoriesRepresentedInObjects(ArrayList<String> objectCategories, ArrayList<String> objects){// TODO: alternative to ontology missing query
        boolean categoryFound = false;
        try{
            if(!objectCategories.containsAll(objects)){
                for(int i=0; i<objects.size() && !categoryFound; i++)
                    if(objectCategories.contains(objects.get(i)) || categoriesAreRepresentedInObject( objectCategories, objects.get(i)))
                        categoryFound = true;
            }
            else categoryFound = true;
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in categoriesRepresentedInObjects: " + e.getMessage());
        }
        return categoryFound;
    }

    static protected boolean categoriesAreRepresentedInObject(ArrayList<String> categoryClasses, String objectClass){ // milk -- cup, medicinebox
        // For objects only for now. // TODO: alternative to ontology missing query
        boolean exit = false;
        try{
            for(int i=0; i<categoryClasses.size() && !exit; ++i)
                if(objectBelongsToCategory(objectClass, categoryClasses.get(i)) || categoryClasses.get(i).equals(objectClass)){
                    exit = true;
                }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in objectBelongsToCategories: "+ objectClass+" category asked does not exist. Error:" + e.getMessage());
        }
        return exit;
    }

    static protected boolean objectBelongsToCategories(String objectClass, ArrayList<String> categoryClasses){ // milk -- cup, medicinebox
        // For objects only for now. // TODO: alternative to ontology missing query
        boolean exit = false;
        try{
            for(int i=0; i<categoryClasses.size() && !exit; ++i)
                if(objectBelongsToCategory(objectClass, categoryClasses.get(i))){
                    exit = true;
                }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in objectBelongsToCategories: "+ objectClass+" category asked does not exist. Error:" + e.getMessage());
        }
        return exit;
    }

    static protected boolean objectBelongsToCategory(String objectClass, String categoryClass){
        // For objects only for now. // TODO: alternative to ontology missing query
        try{
            if(objectCategories.keySet().contains(categoryClass)){
                if (objectCategories.get(categoryClass).contains(objectClass)){  //System.out.println(objectClass+" BELONGS TO STACKABLE OBJECT");
                    return true;
                }
                else{  //System.out.println(objectClass+" DOES NOT BELONG TO STACKABLE OBJECT");
                    return false;
                }
            }
            else{
                if(objectClass.equals(categoryClass)) //TODO: change order
                    return true;
                else{
                    //System.out.println("Error in objectBelongsToCategory: "+categoryClass+" category asked does not exist");
                    return false;
                    //System.exit(0);
                }
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error in objectBelongsToCategory: category asked does not exist. Error:" + e.getMessage());
        }
        return false;
    }
      
    static protected float euclideanDistance3D(float[] pos1, float[] pos2){
        float dX = pos1[0]- pos2[0];
        float dY = pos1[1]- pos2[1];
        float dZ = pos1[2]- pos2[2];
        return (float)Math.sqrt((dX * dX) + (dY * dY) + (dZ * dZ));
    }

    static protected float horizontalXDistance(float[] pos1, float[] pos2){
        return Math.abs(pos1[0]-pos2[0]);
    }

    static protected float horizontalZDistance(float[] pos1, float[] pos2){
        return Math.abs(pos1[2]-pos2[2]);
    }

    static protected float verticalDistance(float[] pos1, float[] pos2){
        return Math.abs(pos1[1]-pos2[1]);
    }

    //Returns True if one subActiv precedes other by [1 frame, max_duration(All null activities)]
    static protected boolean precedes(Dictionary subActivity1, Dictionary subActivity2){
        int difference = (Integer)subActivity2.get("startFrame")- (Integer)subActivity1.get("endFrame");
        if (difference > 0 && difference < precedesThresholdInFrames)
            return true;
        else return false;
    }

    public float[] precisionAndRecall(int totalElements, int nRetrieved, int nRelevant) {
        int A = nRelevant;
        int B = totalElements-nRelevant;
        int C = nRetrieved-nRelevant;
        float precision = new Float((A / (A+C) ) * 100.0);
        float recall = new Float((A / (A+B) ) * 100.0);
        return new float[]{precision, recall};
    }

    protected static float KBSizeInMinutes (){
        float frameNumberNow = 0;
        if(KBmirrorSubActivities.size()>0){
            // frameNumberNow = (int) KBmirrorSubActivities.get(KBmirrorSubActivities.size()-1).get("endFrame"); // TODO: to minutes
            //LocalDate KBInitializedDate = LocalDate.now();
            //Period KBAge= Period.between(KBInitializedDate, now);
            //long p2 = ChronoUnit.MINUTES.between(KBInitializedDate, now);
            //System.out.println("KB is " + KBAge.getYears() + " years, " + p2 + " minutes");//+ KBAge.getMinutes() +   " minutes";
        }
        return frameNumberNow;
    }

    protected static int KBSizeInFrames (){
        int latestFrameNr = 0;
        if(KBmirrorSubActivities.size()>0){
            latestFrameNr = (Integer)(KBmirrorSubActivities.get(KBmirrorSubActivities.size()-1).get("endFrame"));
            return (latestFrameNr - ((Integer)KBmirrorSubActivities.get(0).get("endFrame"))); //currentFrame?
        }
        return latestFrameNr;
    }

    protected static void configureActivitiesAndInitializeKB() throws FuzzyOntologyException,IOException, ParseException, InconsistentOntologyException{
        // add HIGH LEVEL activities
        activities.put(CEREAL, 0);
        activities.put(MEDICINE, 1);
        activities.put(STACKING, 2);
        activities.put(UNSTACKING, 3);
        activities.put(MICROWAVING, 4);
        activities.put(BENDING, 5);
        activities.put(CLEANING_OBJECTS, 6);
        activities.put(TAKEOUT, 7);
        activities.put(ARRANGING_OBJECTS, 8);
        activities.put(EATING_MEAL, 9);

        indexes.put(0, CEREAL);
        indexes.put(1, MEDICINE);
        indexes.put(2, STACKING);  // Object positions are needed to distinguish among these two activities (stacking and unstacking)
        indexes.put(3, UNSTACKING);
        indexes.put(4, MICROWAVING); // Microwaving food
        indexes.put(5, BENDING); // = Picking objects
        indexes.put(6, CLEANING_OBJECTS); // cleaningObjects in the fileKB (referred as scrubbing in the paper)
        indexes.put(7, TAKEOUT); // =Taking food
        indexes.put(8, ARRANGING_OBJECTS); // = Arranging objects in the paper (placing in the training file)
        indexes.put(9, EATING_MEAL); // eatingMeal in the fileKB (eating in the training file)

        groupsOfChallengingSimilarActivities.add(CLEANING_OBJECTS); //TODO: new ArrayList<String>(Arrays.asList(CLEANING_OBJECTS, MICROWAVING, TAKEOUT)));
        groupsOfChallengingSimilarActivities.add(MICROWAVING);
        groupsOfChallengingSimilarActivities.add(TAKEOUT);
        // SUBSUMPTION PAIRS ARE ADDED IN ORDER OF DECREASING SUBSUMPTION GENERALITY; THUS, THE FIRST ONE BEING FULFILLED IS RETURNED
        //subsumesPairsBySubAct.add(new ArrayList<String>(Arrays.asList(MICROWAVING, CLEANING_OBJECTS))); // TODO: considering all but one different sub-activity in the sequence
        //subsumesPairsBySubAct.add(new ArrayList<String>(Arrays.asList(TAKEOUT, CLEANING_OBJECTS)));
        subsumesPairsBySubAct.add(new ArrayList<String>(Arrays.asList(TAKEOUT, MICROWAVING)));
        subsumesPairsBySubAct.add(new ArrayList<String>(Arrays.asList(BENDING, ARRANGING_OBJECTS)));
        //subsumesPairsBySubAct.add(new ArrayList<String>(Arrays.asList(UNSTACKING, STACKING))); // TODO NEEDED?
        //subsumesPairsBySubAct.add(new ArrayList<String>(Arrays.asList(STACKING, UNSTACKING)));

        // OBJECT CATEGORIES- OBJECTS ASSOCIATIONS
        //10 objects: 'book','bowl','box','cloth','cup','medcinebox','microwave','milk','plate','remote'
        //objectCategories.put(KITCHENWARE, new ArrayList<String>(Arrays.asList(BOWL, CUP, PLATE))); // Cloth out
        //objectCategories.put(EDIBLE, new ArrayList<String>(Arrays.asList(MILK, MEDICINE_BOX)));
        objectCategories.put(STACKABLE, new ArrayList<String>(Arrays.asList(BOWL, PLATE, BOX)));
        objectCategories.put(MOVABLE, new ArrayList<String>(Arrays.asList(BOWL, CUP, MEDICINE_BOX, PLATE, CLOTH, BOX, BOOK, MILK, REMOTE )));
        objectCategories.put(DRINKING_KITCHENWARE, new ArrayList<String>(Arrays.asList(CUP, BOWL))); //plus box used in microwawing
        objectCategories.put(CONTAINER_KITCHENWARE, new ArrayList<String>(Arrays.asList(CUP, BOWL, PLATE, BOX))); //
        objectCategories.put(PICKABLE, new ArrayList<String>(Arrays.asList(BOX, REMOTE, BOWL, BOOK, CUP)));
        objectCategories.put(ARRANGEABLE, new ArrayList<String>(Arrays.asList(BOOK, BOX)));
        //System.out.println("Belongs to category: "+belongsToCategory (DRINKING_KITCHENWARE, BOOK));
        //reachMilkOrBowlOrBox, moveMilkOrBowlOrBox, placeMilkOrBowlOrBox, openMilkOrBox, pourMilkOrBox, reachCupOrMedicineBox, moveCupOrMedicineBox, placeCupOrMedicineBox, openMedicineBox, eatMedicineBox, drinkCup, reachStackable, moveStackable, placeStackable, nullSA, reachMicroOrDrinkingKitchenware, moveDrinkingKitchenware, placeDrinkingKitchenware, openMicro, closeMicro, reachPickable, movePickable, reachCleaningMaterial, moveCloth, placeCloth, cleanMicroOrCloth,
        //reachContainerKitchenwareOrMicro, moveContainerKitchenware, placeContainerKitchenware, reachArrangeable, moveArrangeable, placeArrangeable, arrangingObjects, reachCup, moveCup, placeCup, eatCup;
        reachMicroOrCloth = new HashMap<String, ArrayList<String>>();
        moveCloth = new HashMap<String, ArrayList<String>>();
        placeCloth = new HashMap<String, ArrayList<String>>();
        cleanMicroOrCloth = new HashMap<String, ArrayList<String>>();
        reachMicroOrDrinkingKitchenwareOrBox = new HashMap<String, ArrayList<String>>();
        moveDrinkingKitchenwareOrBox = new HashMap<String, ArrayList<String>>();
        placeDrinkingKitchenwareOrBox = new HashMap<String, ArrayList<String>>();
        openMicro = new HashMap<String, ArrayList<String>>();
        closeMicro = new HashMap<String, ArrayList<String>>();
        reachMicro = new HashMap<String, ArrayList<String>>();
        reachPickable = new HashMap<String, ArrayList<String>>();
        movePickable = new HashMap<String, ArrayList<String>>();
        openMedicineBox = new HashMap<String, ArrayList<String>>();
        eatMedicineBox = new HashMap<String, ArrayList<String>>();
        reachStackable = new HashMap<String, ArrayList<String>>();
        moveStackable = new HashMap<String, ArrayList<String>>();
        placeStackable = new HashMap<String, ArrayList<String>>();
        nullSA = new HashMap<String, ArrayList<String>>();  // HANDLE
        drinkCup = new HashMap<String, ArrayList<String>>();
        reachMilkOrBowlOrBox = new HashMap<String, ArrayList<String>>();
        moveMilkOrBowlOrBox = new HashMap<String, ArrayList<String>>();
        placeMilkOrBowlOrBox = new HashMap<String, ArrayList<String>>();
        openMilkOrBox = new HashMap<String, ArrayList<String>>();
        pourMilkOrBox = new HashMap<String, ArrayList<String>>();
        reachCupOrMedicineBox = new HashMap<String, ArrayList<String>>();
        moveCupOrMedicineBox = new HashMap<String, ArrayList<String>>();
        placeCupOrMedicineBox = new HashMap<String, ArrayList<String>>();
        reachContainerKitchenwareOrMicro = new HashMap<String, ArrayList<String>>();
        moveContainerKitchenware = new HashMap<String, ArrayList<String>>();
        placeContainerKitchenware = new HashMap<String, ArrayList<String>>();
        reachArrangeable = new HashMap<String, ArrayList<String>>();
        moveArrangeable = new HashMap<String, ArrayList<String>>();
        placeArrangeable = new HashMap<String, ArrayList<String>>();
        reachCup = new HashMap<String, ArrayList<String>>();
        moveCup = new HashMap<String, ArrayList<String>>();
        placeCup = new HashMap<String, ArrayList<String>>();
        eatCup = new HashMap<String, ArrayList<String>>();

        // SubActivity-Objects pairs for first Order filter:
        reachMicroOrCloth.put(REACHING, new ArrayList<String>(Arrays.asList(CLOTH, MICROWAVE ))); // TODO: Optimize having one only hashmap
        moveCloth.put(MOVING, new ArrayList<String>(Arrays.asList(CLOTH )));
        placeCloth.put(PLACING, new ArrayList<String>(Arrays.asList(CLOTH )));
        cleanMicroOrCloth.put(CLEANING, new ArrayList<String>(Arrays.asList(MICROWAVE, CLOTH )));
        reachMicroOrDrinkingKitchenwareOrBox.put(REACHING, new ArrayList<String>(Arrays.asList(MICROWAVE, DRINKING_KITCHENWARE, BOX )));
        moveDrinkingKitchenwareOrBox.put(MOVING, new ArrayList<String>(Arrays.asList(DRINKING_KITCHENWARE, BOX)));
        placeDrinkingKitchenwareOrBox.put(PLACING, new ArrayList<String>(Arrays.asList(DRINKING_KITCHENWARE, BOX)));
        openMicro.put(OPENING, new ArrayList<String>(Arrays.asList(MICROWAVE )));
        closeMicro.put(CLOSING, new ArrayList<String>(Arrays.asList(MICROWAVE )));
        reachMicro.put(REACHING, new ArrayList<String>(Arrays.asList(MICROWAVE )));
        reachPickable.put(REACHING, new ArrayList<String>(Arrays.asList(PICKABLE )));
        movePickable.put(MOVING, new ArrayList<String>(Arrays.asList( PICKABLE)));
        openMedicineBox.put(OPENING, new ArrayList<String>(Arrays.asList( MEDICINE_BOX)));
        eatMedicineBox.put(EATING, new ArrayList<String>(Arrays.asList( MEDICINE_BOX)));
        reachStackable.put(REACHING, new ArrayList<String>(Arrays.asList(STACKABLE )));
        moveStackable.put(MOVING, new ArrayList<String>(Arrays.asList( STACKABLE)));
        placeStackable.put(PLACING, new ArrayList<String>(Arrays.asList(STACKABLE)));
        nullSA.put(NULL_SA, new ArrayList<String>(Arrays.asList(NULL_OBJECT)));  // HANDLE
        drinkCup.put(DRINKING, new ArrayList<String>(Arrays.asList(CUP)));
        reachMilkOrBowlOrBox.put(REACHING, new ArrayList<String>(Arrays.asList(MILK, BOWL, BOX )));
        moveMilkOrBowlOrBox.put(MOVING, new ArrayList<String>(Arrays.asList(MILK, BOWL, BOX )));
        placeMilkOrBowlOrBox.put(PLACING, new ArrayList<String>(Arrays.asList(MILK, BOWL, BOX )));
        openMilkOrBox.put(OPENING, new ArrayList<String>(Arrays.asList(MILK, BOX )));
        pourMilkOrBox.put(POURING, new ArrayList<String>(Arrays.asList(MILK, BOX )));
        reachCupOrMedicineBox.put(REACHING, new ArrayList<String>(Arrays.asList(CUP, MEDICINE_BOX )));
        moveCupOrMedicineBox.put(MOVING, new ArrayList<String>(Arrays.asList(CUP, MEDICINE_BOX )));
        placeCupOrMedicineBox.put(PLACING, new ArrayList<String>(Arrays.asList(CUP, MEDICINE_BOX )));
        reachContainerKitchenwareOrMicro.put(REACHING, new ArrayList<String>(Arrays.asList(CONTAINER_KITCHENWARE, MICROWAVE )));
        moveContainerKitchenware.put(MOVING, new ArrayList<String>(Arrays.asList(CONTAINER_KITCHENWARE )));
        placeContainerKitchenware.put(PLACING, new ArrayList<String>(Arrays.asList(CONTAINER_KITCHENWARE )));
        reachArrangeable.put(REACHING, new ArrayList<String>(Arrays.asList(ARRANGEABLE )));
        moveArrangeable.put(MOVING, new ArrayList<String>(Arrays.asList(ARRANGEABLE )));
        placeArrangeable.put(PLACING, new ArrayList<String>(Arrays.asList(ARRANGEABLE )));
        reachCup.put(REACHING, new ArrayList<String>(Arrays.asList(CUP )));
        moveCup.put(MOVING, new ArrayList<String>(Arrays.asList(CUP )));
        placeCup.put(PLACING, new ArrayList<String>(Arrays.asList(CUP )));
        eatCup.put(EATING, new ArrayList<String>(Arrays.asList(CUP )));
        //orderObjectsConstraints.add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachMicroOrDrinkingKitchenwareOrBox, openMicro, reachMicroOrDrinkingKitchenwareOrBox, moveDrinkingKitchenwareOrBox, placeDrinkingKitchenwareOrBox, reachMicroOrDrinkingKitchenwareOrBox, closeMicro, nullSA))); // microwaving general
        //orderObjectsConstraints.add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachContainerKitchenwareOrMicro, openMicro, reachContainerKitchenwareOrMicro, moveContainerKitchenware, placeContainerKitchenware, reachContainerKitchenwareOrMicro, closeMicro))); //takeout general

        orderObjectsConstraints.add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(moveMilkOrBowlOrBox, placeMilkOrBowlOrBox, reachMilkOrBowlOrBox, openMilkOrBox, reachMilkOrBowlOrBox, moveMilkOrBowlOrBox, pourMilkOrBox, moveMilkOrBowlOrBox, placeMilkOrBowlOrBox, reachMilkOrBowlOrBox, moveMilkOrBowlOrBox, pourMilkOrBox, moveMilkOrBowlOrBox, placeMilkOrBowlOrBox)));// removed openMilkOrBox (1 appearance in all dataset)
        orderObjectsConstraints.add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(reachCupOrMedicineBox, openMedicineBox, reachCupOrMedicineBox, moveCupOrMedicineBox, eatMedicineBox, reachCupOrMedicineBox, moveCupOrMedicineBox, drinkCup, moveCupOrMedicineBox, placeCupOrMedicineBox)));
        orderObjectsConstraints.add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachStackable, moveStackable, placeStackable, reachStackable, moveStackable, placeStackable, nullSA)));//REPEATED
        orderObjectsConstraints.add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachStackable, moveStackable, placeStackable, reachStackable, moveStackable, placeStackable, nullSA)));
        //orderObjectsConstraints.add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachMicro, openMicro, reachMicroOrDrinkingKitchenwareOrBox, moveDrinkingKitchenwareOrBox, placeDrinkingKitchenwareOrBox, reachMicro, closeMicro, nullSA))); // microwaving concretized
        orderObjectsConstraints.add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachMicroOrDrinkingKitchenwareOrBox, openMicro, reachMicroOrDrinkingKitchenwareOrBox, moveDrinkingKitchenwareOrBox, placeDrinkingKitchenwareOrBox, reachMicroOrDrinkingKitchenwareOrBox, closeMicro, nullSA))); // microwaving general
        orderObjectsConstraints.add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachPickable, movePickable)));//, nullSA))); // bending
        orderObjectsConstraints.add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachMicroOrCloth, openMicro, reachMicroOrCloth, moveCloth, cleanMicroOrCloth, moveCloth, placeCloth, reachMicroOrCloth, closeMicro)));// Order to be completed
        //orderObjectsConstraints.add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachMicro, openMicro, reachContainerKitchenwareOrMicro, moveContainerKitchenware, placeContainerKitchenware, reachMicro, closeMicro))); //takeout
        orderObjectsConstraints.add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachContainerKitchenwareOrMicro, openMicro, reachContainerKitchenwareOrMicro, moveContainerKitchenware, placeContainerKitchenware, reachContainerKitchenwareOrMicro, closeMicro))); //takeout general
        orderObjectsConstraints.add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachArrangeable, moveArrangeable, placeArrangeable))); // Arrange objects
        orderObjectsConstraints.add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(moveCup, eatCup, moveCup, nullSA, moveCup, eatCup, moveCup, nullSA, moveCup, drinkCup, moveCup, reachCup, placeCup))); // ** too many moveCups?
        // microw (define-concept antecedent5 (w-sum (0.32 reachMicroOrDrinkingKitchenwareOrBox)(0.11 moveDrinkingKitchenwareOrBox)(0.11 placeDrinkingKitchenwareOrBox)(0.12 openMicro)(0.11 closeMicro)(0.23 nullSA)))
        // takeout (define-concept antecedent8 (w-sum (0.38 reachContainerKitchenwareOrMicro)(0.12 moveContainerKitchenware)(0.12 placeContainerKitchenware)(0.13 openMicro)(0.13 closeMicro)(0.12 nullSA)))
        // arrangingObj (define-concept antecedent9 (w-sum (0.23 reachArrangeable) (0.27 moveArrangeable)(0.25 placeArrangeable)(0.25 nullSA)))

        // SUB-ACTIVITIES SUB-SEQUENCES CONSTRAINTS
        SubActivitiesSubSequence cereal1 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(moveMilkOrBowlOrBox, placeMilkOrBowlOrBox)), false, 1);
        SubActivitiesSubSequence cereal2 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(reachMilkOrBowlOrBox, openMilkOrBox)), false, 1);
        SubActivitiesSubSequence cereal3 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(reachMilkOrBowlOrBox, moveMilkOrBowlOrBox, pourMilkOrBox, moveMilkOrBowlOrBox, placeMilkOrBowlOrBox)), false,  1);
        SubActivitiesSubSequence cereal4 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(reachMilkOrBowlOrBox, moveMilkOrBowlOrBox, pourMilkOrBox, moveMilkOrBowlOrBox, placeMilkOrBowlOrBox)), false,  1);
        orderObjectsSubSequenceConstraints.add(new ArrayList<SubActivitiesSubSequence>(Arrays.asList(cereal1, cereal2, cereal3, cereal4)));
        userPerformsSubActivitiesObjectAxioms.add(new ArrayList<String>(Arrays.asList("reachMilkOrBowlOrBox", "moveMilkOrBowlOrBox", "placeMilkOrBowlOrBox", "openMilkOrBox", "close", "eat", "drink", "pourMilkOrBox", "clean", "nullSA")));

        SubActivitiesSubSequence medicine1 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(reachCupOrMedicineBox, openMedicineBox)), false,  1);
        SubActivitiesSubSequence medicine2 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(reachCupOrMedicineBox, moveCupOrMedicineBox, eatMedicineBox)), false,  1);
        SubActivitiesSubSequence medicine3 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(reachCupOrMedicineBox, moveCupOrMedicineBox, drinkCup, moveCupOrMedicineBox, placeCupOrMedicineBox)), false,  1);
        orderObjectsSubSequenceConstraints.add(new ArrayList<SubActivitiesSubSequence>(Arrays.asList(medicine1, medicine2, medicine3)));
        userPerformsSubActivitiesObjectAxioms.add(new ArrayList<String>(Arrays.asList("reachCupOrMedicineBox", "moveCupOrMedicineBox", "placeCupOrMedicineBox", "openMedicineBox", "close", "eatMedicineBox", "drinkCup", "pour", "clean", "nullSA")));

        SubActivitiesSubSequence stacking1 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(nullSA)), false,  1);
        SubActivitiesSubSequence stacking2 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(reachStackable, moveStackable, placeStackable)), false, 3);
        SubActivitiesSubSequence stacking3 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(nullSA)), false,  1);
        orderObjectsSubSequenceConstraints.add(new ArrayList<SubActivitiesSubSequence>(Arrays.asList(stacking1, stacking2, stacking3)));
        userPerformsSubActivitiesObjectAxioms.add(new ArrayList<String>(Arrays.asList("reachStackable", "moveStackable", "placeStackable", "open", "close", "eat", "drink", "pour", "clean", "nullSA")));
        orderObjectsSubSequenceConstraints.add(new ArrayList<SubActivitiesSubSequence>(Arrays.asList(stacking1, stacking2, stacking3)));
        userPerformsSubActivitiesObjectAxioms.add(new ArrayList<String>(Arrays.asList("reachStackable", "moveStackable", "placeStackable", "open", "close", "eat", "drink", "pour", "clean", "nullSA")));

        SubActivitiesSubSequence microwaving1 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(nullSA, reachMicroOrDrinkingKitchenwareOrBox, openMicro)), false,  1);
        SubActivitiesSubSequence microwaving2 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(reachMicroOrDrinkingKitchenwareOrBox, moveDrinkingKitchenwareOrBox, placeDrinkingKitchenwareOrBox)), false,  1);
        SubActivitiesSubSequence microwaving3 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(reachMicroOrDrinkingKitchenwareOrBox, closeMicro, nullSA)), false,  1);
        orderObjectsSubSequenceConstraints.add(new ArrayList<SubActivitiesSubSequence>(Arrays.asList(microwaving1, microwaving2, microwaving3)));
        userPerformsSubActivitiesObjectAxioms.add(new ArrayList<String>(Arrays.asList("reachMicroOrDrinkingKitchenwareOrBox", "moveDrinkingKitchenwareOrBox", "placeDrinkingKitchenwareOrBox", "openMicro", "closeMicro", "eat", "drink", "pour", "clean", "nullSA")));

        SubActivitiesSubSequence bending = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(nullSA, reachPickable, movePickable)), false,  1);
        orderObjectsSubSequenceConstraints.add(new ArrayList<SubActivitiesSubSequence>(Arrays.asList(bending))); // ** too many moveCups?
        userPerformsSubActivitiesObjectAxioms.add(new ArrayList<String>(Arrays.asList("reachPickable", "movePickable", "place", "open", "close", "eat", "drink", "pour", "clean", "nullSA")));

        SubActivitiesSubSequence cleaning1 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(nullSA, reachMicroOrCloth, openMicro)), false,  1);
        SubActivitiesSubSequence cleaning2 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(reachMicroOrCloth, moveCloth, cleanMicroOrCloth)), false,  1);
        SubActivitiesSubSequence cleaning3 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(moveCloth, placeCloth)), false,  1);
        SubActivitiesSubSequence cleaning4 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(reachMicroOrCloth, closeMicro)), false,  1);
        orderObjectsSubSequenceConstraints.add(new ArrayList<SubActivitiesSubSequence>(Arrays.asList(cleaning1, cleaning2, cleaning3, cleaning4)));
        userPerformsSubActivitiesObjectAxioms.add(new ArrayList<String>(Arrays.asList("reachMicroOrCloth", "moveCloth", "placeCloth", "openMicro", "closeMicro", "eat", "drink", "pour", "cleanMicroOrCloth", "nullSA")));

        SubActivitiesSubSequence takeout1 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(nullSA, reachContainerKitchenwareOrMicro, openMicro)), false,  1);
        SubActivitiesSubSequence takeout2 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(reachContainerKitchenwareOrMicro, moveContainerKitchenware, placeContainerKitchenware)), false,  1);
        SubActivitiesSubSequence takeout3 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(reachContainerKitchenwareOrMicro, closeMicro)), false,  1);
        orderObjectsSubSequenceConstraints.add(new ArrayList<SubActivitiesSubSequence>(Arrays.asList(takeout1, takeout2, takeout3)));
        userPerformsSubActivitiesObjectAxioms.add(new ArrayList<String>(Arrays.asList("reachContainerKitchenwareOrMicro", "moveContainerKitchenware", "placeContainerKitchenware", "openMicro", "closeMicro", "eat", "drink", "pour", "clean", "nullSA")));

        SubActivitiesSubSequence arrangingObjects1 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(nullSA, reachArrangeable, moveArrangeable, placeArrangeable)), false,  1);// separate nulls?
        orderObjectsSubSequenceConstraints.add(new ArrayList<SubActivitiesSubSequence>(Arrays.asList(arrangingObjects1)));
        userPerformsSubActivitiesObjectAxioms.add(new ArrayList<String>(Arrays.asList("reachArrangeable", "moveArrangeable", "placeArrangeable", "open", "close", "eat", "drink", "pour", "clean", "nullSA")));

        SubActivitiesSubSequence eatingMeal1 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(reachCup)), true, 1);
        SubActivitiesSubSequence eatingMeal2 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(moveCup, eatCup, moveCup)), false, 2);
        SubActivitiesSubSequence eatingMeal3 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(nullSA)), true, 1);
        SubActivitiesSubSequence eatingMeal4 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(reachCup)), true, 1);
        SubActivitiesSubSequence eatingMeal5 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(moveCup, drinkCup, moveCup)), false, 2);
        SubActivitiesSubSequence eatingMeal6 = new SubActivitiesSubSequence(new ArrayList<HashMap<String, ArrayList<String>>> (Arrays.asList(placeCup)), true, 1);
        orderObjectsSubSequenceConstraints.add(new ArrayList<SubActivitiesSubSequence>(Arrays.asList(eatingMeal1, eatingMeal2,eatingMeal3,eatingMeal4,eatingMeal5,eatingMeal6)));
        userPerformsSubActivitiesObjectAxioms.add(new ArrayList<String>(Arrays.asList("reachCup", "moveCup", "placeCup", "open", "close", "eatCup", "drinkCup", "pour", "clean", "nullSA")));

        // USED IN MIN. DIFFERENT OBJECT CATEGORIES RATIO:
        objectCategoriesConstraints.add(new ArrayList<String>(Arrays.asList(MILK, BOWL, BOX)));
        objectCategoriesConstraints.add(new ArrayList<String>(Arrays.asList(CUP, MEDICINE_BOX)));
        objectCategoriesConstraints.add(new ArrayList<String>(Arrays.asList(STACKABLE)));//add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachStackable, moveStackable, placeStackable, reachStackable, moveStackable, placeStackable, nullSA)));//REPEATED
        objectCategoriesConstraints.add(new ArrayList<String>(Arrays.asList(STACKABLE)));//add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachStackable, moveStackable, placeStackable, reachStackable, moveStackable, placeStackable, nullSA)));
        objectCategoriesConstraints.add(new ArrayList<String>(Arrays.asList(MICROWAVE, DRINKING_KITCHENWARE, BOX)));//add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachMicroOrDrinkingKitchenwareOrBox, openMicro, reachMicroOrDrinkingKitchenwareOrBox, moveDrinkingKitchenwareOrBox, placeDrinkingKitchenwareOrBox, reachMicroOrDrinkingKitchenwareOrBox, closeMicro, nullSA))); // microwaving general
        objectCategoriesConstraints.add(new ArrayList<String>(Arrays.asList(PICKABLE)));//add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachPickable, movePickable)));//, nullSA))); // bending
        objectCategoriesConstraints.add(new ArrayList<String>(Arrays.asList(CLOTH, MICROWAVE)));
        objectCategoriesConstraints.add(new ArrayList<String>(Arrays.asList(CONTAINER_KITCHENWARE, MICROWAVE)));//add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachContainerKitchenwareOrMicro, openMicro, reachContainerKitchenwareOrMicro, moveContainerKitchenware, placeContainerKitchenware, reachContainerKitchenwareOrMicro, closeMicro))); //takeout general
        objectCategoriesConstraints.add(new ArrayList<String>(Arrays.asList(ARRANGEABLE)));//add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(nullSA, reachArrangeable, moveArrangeable, placeArrangeable))); // Arrange objects
        objectCategoriesConstraints.add(new ArrayList<String>(Arrays.asList(CUP)));//add(new ArrayList<HashMap<String, ArrayList<String>>>(Arrays.asList(moveCup, eatCup, moveCup, nullSA, moveCup, eatCup, moveCup, nullSA, moveCup, drinkCup, moveCup, reachCup, placeCup))); // ** too many moveCups?


        Date cycleStartTime = new Date();
        // display time and date using toString()
        System.out.println(cycleStartTime.toString());

        LearnMeanAndStdDevDurationsAndSetPrecedesThreshold(trainingFile);  // From  SHORT training file (REQUIRED FOR COUNTING AVGS DURATION TIMES AND START PARAMS).
        //LearnSubActivitiesInvolvedInActivities(trainingFile);
        //System.out.println("Learned rules: " + LearnWeightsAndCreateRules(trainingFileWithUsers, new ArrayList<Integer>(Arrays.asList(-1)), outputRulesFile));//using all entries

        initializeKB(individualName);
        // Solve a query q
        startQuery = new KbSatisfiableQuery();
        //Query q = new AllInstancesQuery(Lunch);
        startQuerySolution = startQuery.solve(kb);
        if (startQuerySolution.isConsistentKB())  {
            System.out.println("KB is consistent");
            //System.out.println(q1.toString() + result1.getSolution());
        }
        else{
            System.out.println("KB is inconsistent!");
            System.exit(0);
        }
        // Optionally, show the time and language of the KB
        //System.out.println("Time (s): ".concat(String.valueOf(q.getTotalTime())));
        System.out.println("Language: ".concat(kb.getLanguage()));
        kb.solveKB();

        System.out.println("orderObjectConst "+ orderObjectsConstraints.size());
        System.out.println("Contained in Bending, ArrangingObjects "+ containedIn(BENDING, ARRANGING_OBJECTS));
        System.out.println("Contained in ArrangingObjects, Bending "+ containedIn(ARRANGING_OBJECTS, BENDING));
        System.out.println("Contained in Takeout, Microwaving "+ containedIn(TAKEOUT, MICROWAVING));
        System.out.println("Contained in Microwaving, Takeout "+ containedIn(MICROWAVING, TAKEOUT));
        System.out.println("Contained in Stacking, Unstacking "+ containedIn(STACKING, UNSTACKING));
        System.out.println("Contained in Medicine, Cereal "+ containedIn(MEDICINE, CEREAL));
    }
}


