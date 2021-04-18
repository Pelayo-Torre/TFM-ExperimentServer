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
	
	public static class PetitionNotRegisteredExperiment{
		
		public static void link(PetitionNotRegistered petition, Experiment experiment) {
			petition.setExperiment(experiment);
			experiment.getPetitionsNotRegistered().add(petition);			
		}
		
		public static void unlink(PetitionNotRegistered petition, Experiment experiment) {
			experiment.getPetitionsNotRegistered().remove(petition);
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
	
	public static class ExperimentDemographicData {
		
		public static void link(Experiment experiment, DemographicData demographicData) {
			demographicData.setExperiment(experiment);
			experiment.getDemographicData().add(demographicData);
		}
		
		public static void unlink(Experiment experiment, DemographicData demographicData) {
			experiment.getDemographicData().remove(demographicData);
			demographicData.setExperiment(null);
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
	
	public static class UserEvent{
		
		public static void link(User user, Event event) {
			event.setUser(user);
			user.getEvents().add(event);
		}
		
		public static void unlink(User user, Event event) {
			user.getEvents().remove(event);
			event.setUser(null);
		}
		
	}
	
	public static class UserComponentData {
		
		public static void link(User user, ComponentData component) {
			component.setUser(user);
			user.getComponents().add(component);
		}
		
		public static void unlink(User user, ComponentData component) {
			user.getComponents().remove(component);
			component.setUser(null);
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
