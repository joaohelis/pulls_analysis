package ufrn.msr.githubapi.importter;

public class GHTorrentImportter {
	
//	public static void issueEventImporter(String filepath){
//		
//		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		
//		Reader in;
//		int count = 0;
//		
//		try {
//			
//			HibernateIssueEventDAO issueEventDAO = new HibernateIssueEventDAO();
//			HibernateIssueDAO issueDAO = new HibernateIssueDAO();
//			
//			issueEventDAO.beginTransaction();
//						
//			in = new FileReader(filepath);
//			
//			System.out.println(" ___________________________________________________________________________________________________");
//			System.out.println("| #     | event_id   | issue_id | actor_id| action       | action_specific                          |");
//			System.out.println("|_______|____________|__________|_________|______________|__________________________________________|");
//			
//			Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().withNullString("").parse(in);			
//			for (CSVRecord record : records) {
//				IssueEvent event = new IssueEvent();
//				
//				Issue issue = null;
//				try{
//					issue = issueDAO.get(Integer.parseInt(record.get("issue_id")));
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//				
//				if(issue == null) continue;
//				
//				event.setAction(record.get("action"));
//				event.setAction_specific(record.get("action_specific"));
//				event.setAuthor_id(Integer.parseInt(record.get("actor_id")));
//				event.setEvent_id(Integer.parseInt(record.get("event_id")));
//				event.setIssue(issue);
//				try {
//					Date d = ft.parse(record.get("created_at"));
//					event.setCreated_at(d);
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//				
//				try{
//					issueEventDAO.save(event);
//				}catch(Exception e){
//					e.printStackTrace();
//					continue;
//				}
//				
//				System.out.println(String.format("| %-5s |  %-9s |  %-7s | %-7s | %-12s | %-40s |" , ++count, 
//							event.getEvent_id(), event.getIssue().getIssue_id(), event.getAuthor_id(), event.getAction(), event.getAction_specific()));
//			}
//			
//			issueEventDAO.commitTransaction();
//						
//			System.out.println(" ___________________________________________________________________________________________________");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e){			
//			e.printStackTrace();
//		}	
//	}
//	
//	
//	public static void issueImporter(String filepath){
//		
//		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		
//		Reader in;
//		int count = 0;
//		
//		try {
//			
//			HibernateIssueDAO issueDAO = new HibernateIssueDAO();			
//			issueDAO.beginTransaction();
//						
//			in = new FileReader(filepath);
//			
//			System.out.println(" _________________________________________________");
//			System.out.println("| #     | issue_id | comments | comments_interval |");
//			System.out.println("|_______|__________|__________|___________________|");
//			
//			Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().withNullString("").parse(in);			
//			for (CSVRecord record : records) {
//				Issue issue = new Issue();
//				
//				String reporterId = record.get("reporter_id");			    
//				String commentsInterval = record.get("comments_interval_avg");
//				
//				if(commentsInterval == null || reporterId == null)
//			    	continue;
//								
//				issue.setIssue_id(Integer.parseInt(record.get("issue_id")));
//			    issue.setRepo_id(Integer.parseInt(record.get("repo_id")));			    
//			    issue.setReporter_id((reporterId != null)? Integer.parseInt(reporterId): null);
//			    issue.setIssueCommentsCount(Integer.parseInt(record.get("comments")));
//			    			    
//			    issue.setIssueCommentsInterval(Float.parseFloat(record.get("comments_interval_avg")));
//			    						    			    			    				
//				try {
//					Date d = ft.parse(record.get("created_at"));
//					issue.setOppenedAt(d);
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//				
//				issueDAO.save(issue);							
//				
//				System.out.println(String.format("| %-5s |  %-7s |  %-7s | %-17s |" , ++count, 
//							issue.getIssue_id(), issue.getIssueCommentsCount(), issue.getIssueCommentsInterval()));
//			}
//			
//			issueDAO.commitTransaction();
//						
//			System.out.println("|_________________________________________________|");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e){			
//			e.printStackTrace();
//		}	
//	}
		
	public static void main(String[] args) {
		
//		String path = "/home/joaohelis/Desktop/delivery_delay/"
//				+ "delivery_delay_projects/facebook-react-native/issues.csv";		
//		issueImporter(path);
		
//		String path = "/home/joaohelis/Desktop/delivery_delay/"
//				+ "delivery_delay_projects/facebook-react-native/issue_events.csv";		
//		issueEventImporter(path);
		
	}
}
