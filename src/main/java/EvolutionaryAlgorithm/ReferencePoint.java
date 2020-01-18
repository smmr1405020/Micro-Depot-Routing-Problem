package EvolutionaryAlgorithm;

import deliveryRoutes.Results;

import java.util.ArrayList;

import static java.lang.Math.sqrt;

public class ReferencePoint {
    public double referenceEmission;
    public double referenceCost;
    public double referenceDistance;
    public double referenceTime;
    public double referenceVehicle;
    public int nicheCount;
    public Results nearestSolution;
    public double nearestSolutionDistance;
    public int nicheCountNew;
    public ReferencePoint(double referenceCost, double referenceEmission){
        this.referenceCost=referenceCost;
        this.referenceEmission =referenceEmission;
        nicheCount= 0;
        nicheCountNew=0;
        nearestSolution = null;
    }

    public ReferencePoint(double referenceCost, double referenceEmission,
                          double referenceTime, double referenceVehicle, double referenceDistance){
        this.referenceCost=referenceCost;
        this.referenceEmission =referenceEmission;
        this.referenceTime = referenceTime;
        this.referenceVehicle = referenceVehicle;
        this.referenceDistance = referenceDistance;
        nicheCount= 0;
        nicheCountNew=0;
        nearestSolution = null;
    }


    public void clearAll(){
        nicheCount = 0;
        nicheCountNew = 0;
        nearestSolution = null;
        nearestSolutionDistance = Double.MAX_VALUE;
    }

    public void setNicheCountNew(int nicheCountNew) {
        this.nicheCountNew = nicheCountNew;
    }

    public int getNicheCountNew() {
        return nicheCountNew;
    }

    public void setNearestSolutionDistance(double nearestSolutionDistance) {
        this.nearestSolutionDistance = nearestSolutionDistance;
    }

    public void setReferenceDistance(double referenceDistance) {
        this.referenceDistance = referenceDistance;
    }

    public void setReferenceTime(double referenceTime) {
        this.referenceTime = referenceTime;
    }

    public void setReferenceVehicle(double referenceVehicle) {
        this.referenceVehicle = referenceVehicle;
    }

    public double getReferenceDistance() {
        return referenceDistance;
    }

    public double getReferenceTime() {
        return referenceTime;
    }

    public double getReferenceVehicle() {
        return referenceVehicle;
    }

    public double getNearestSolutionDistance() {
        return nearestSolutionDistance;
    }

    public void setReferenceCost(double referenceCost) {
        this.referenceCost = referenceCost;
    }

    public void setReferenceEmission(double referenceEmission) {
        this.referenceEmission = referenceEmission;
    }

    public double getReferenceCost() {
        return referenceCost;
    }

    public double getReferenceEmission() {
        return referenceEmission;
    }

    public int getNicheCount() {
        return nicheCount;
    }

    public void setNicheCount(int nicheCount) {
        this.nicheCount = nicheCount;
    }

    public void setNearestSolution(Results nearestSolution) {
        this.nearestSolution = nearestSolution;
    }

    public Results getNearestSolution() {
        return nearestSolution;
    }

    public Results distanceFromSolution(Results res){
        double Re = referenceEmission;
        double Rc = referenceCost;
        double c = res.getMyReferenceCost();
        double e = res.getMyReferenceEmission();
        double distance = (Math.abs(Re*c - Rc*e))/(sqrt(Re*Re + Rc*Rc));

        res.updateReference(this,distance);

        //nearest solution must be set after all distances measured


        return res;
    }

    public ArrayList<Double> scalarMultiplyVector(ArrayList<Double>arr1,double t){
        ArrayList<Double> answer = new ArrayList<>();
        for(int i=0;i<arr1.size();i++){
            answer.add(t*arr1.get(i));
        }
        return answer;
    }
    public double dotProduct(ArrayList<Double>arr1,ArrayList<Double>arr2){
        double dp = 0;
        for(int i=0;i<arr1.size();i++){
            dp += arr1.get(i)*arr2.get(i);
        }
        return dp;
    }
    public ArrayList<Double> substractVector(ArrayList<Double>arr1,ArrayList<Double>arr2){
        ArrayList<Double> answer = new ArrayList<>();
        for(int i=0;i<arr1.size();i++){
            answer.add(arr1.get(i) - arr2.get(i));
        }
        return answer;
    }
    public double lengthOfVector(ArrayList<Double>arr1){
        double len = 0;
        for(int i=0;i<arr1.size();i++){
            len += arr1.get(i)*arr1.get(i);
        }
        return sqrt(len);
    }


    public Results distanceFromSolution_fivedim(Results res){
        ArrayList<Double> B = new ArrayList<>();

        B.add(referenceCost);
        B.add(referenceEmission);
        B.add(referenceTime);
        B.add(referenceVehicle);
        B.add(referenceDistance);

        ArrayList<Double> P = new ArrayList<>();

        P.add(res.getMyReferenceCost());
        P.add(res.getMyReferenceEmission());
        P.add(res.getMyReferenceTime());
        P.add(res.getMyReferenceVehicle());
        P.add(res.getMyReferenceDistance());

        double t = dotProduct(P,B) / dotProduct(B , B);

        double distance = lengthOfVector(substractVector(P,scalarMultiplyVector(B,t)));

        res.updateReference(this,distance);

        //nearest solution must be set after all distances measured

        return res;
    }

    public static boolean isEqual(ReferencePoint r1 , ReferencePoint r2){
        if(r1!=null && r2!=null){
            if(r1.referenceDistance == r2.referenceDistance && r1.referenceCost==r2.referenceCost
            && r1.referenceEmission==r2.referenceEmission && r1.referenceTime==r2.referenceTime
            && r1.referenceVehicle==r2.referenceVehicle){
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return "ReferencePoint{" +
                "referenceEmission=" + referenceEmission +
                ", referenceCost=" + referenceCost +
                ", referenceDistance=" + referenceDistance +
                ", referenceTime=" + referenceTime +
                ", referenceVehicle=" + referenceVehicle +
                ", nicheCount=" + nicheCount +
                ", nearestSolution=" + nearestSolution +
                ", nearestSolutionDistance=" + nearestSolutionDistance +
                ", nicheCountNew=" + nicheCountNew +
                '}';
    }
}
