package com.uniovi.es.model;

public class Associations {
	
	public static class InvestigatorPetitionExperiment{
		
		public static void link(Investigator investigator, Petition petition, Experiment experiment) {
			petition.setInvestigator(investigator);
			petition.setExperiment(experiment);
			
			investigator.getPetitions().add(petition);
			experiment.getPetitions().add(petition);
		}
		
		public static void unlink(Investigator investigator, Petition petition, Experiment experiment) {
			investigator.getPetitions().remove(petition);
			experiment.getPetitions().remove(petition);
			
			petition.setInvestigator(null);
			petition.setExperiment(null);
		}
		
	}
	
	public static class InvestigatorRequest {
		
		public static void link(Investigator investigator, Request request) {
			request.setInvestigator(investigator);
			investigator.getRequests().add(request);
		}
		
		public static void unlink(Investigator investigator, Request request) {
			investigator.getRequests().remove(request);
			request.setInvestigator(null);
		}
		
	}
	
	public static class UserExperiment{
		
		public static void link(User user, Experiment experiment) {
			user.setExperiment(experiment);
			experiment.getUsers().add(user);
		}
		
		public static void unlink(User user, Experiment experiment) {
			experiment.getUsers().remove(user);
			user.setExperiment(null);
		}
		
	}
	
	public static class NoteExperiment{
		
		public static void link(Note note, Experiment experiment) {
			note.setExperiment(experiment);
			experiment.getNotes().add(note);
		}
		
		public static void unlink(Note note, Experiment experiment) {
			experiment.getNotes().remove(note);
			note.setExperiment(null);
		}
		
	}

}
