package com.example.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.dto.ExpenseDTO;
import com.example.entity.ProfileEntity;
import com.example.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
	
	private final ProfileRepository profileRepository;
	private final EmailService emailService;
	
	private final ExpenseService expenseService;
	
	@Value("${money.manager.frontend.url}")
	private String frontendUrl;
	
	@Scheduled(cron = "0 0 22 * * *",zone = "IST")
	public void sendDailyIncomeExpenseReminder() {
		
		log.info("Job started: sendDailyIncomeExpenseReminder()");
		List<ProfileEntity>  profiles= profileRepository.findAll();
		
		for(ProfileEntity profile: profiles) {
			
			String body =
			        "Hi " + profile.getFullNmae() + ",<br><br>"
			      + "This is a friendly reminder to add your income and expense for today in Money Manager,<br><br>"
			      + "<a href='" + frontendUrl + "' "
			      + "style='display:inline-block;padding:10px 20px;"
			      + "background-color:#4CAF50;color:#fff;text-decoration:none;"
			      + "border-radius:5px;font-weight:bold;'>"
			      + "Go to Money Manager</a>"
			      + "<br><br>Best Regards,<br>Money Manager Team";
			emailService.sendEmail(profile.getEmail(), "Daily reminder: Add your income and expenses", body);		
				
		}
		log.info("Job complted:sendDailyIncomeExpenseReminder() ");
	}
	
	@Scheduled(cron = "0 0 23 * * *",zone = "IST")
	public void sendDailyExpenseSummary() {
		log.info("job started: sendDailyExpenseSummary() ");
		List<ProfileEntity> profiles= profileRepository.findAll();
		
		for(ProfileEntity profile:profiles) {
			List<ExpenseDTO> todaysExpenses= expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now(ZoneId.of("Asia/Kolkata")));
			if(!todaysExpenses.isEmpty()) {
				StringBuilder table= new StringBuilder();
				  // Table start
			    table.append("<table style='border-collapse:collapse;width:100%;font-family:Arial,sans-serif;'>");

			    // Header row
			    table.append("<tr style='background-color:#f2f2f2;'>")
			         .append("<th style='border:1px solid #ddd;padding:8px;'>S.No</th>")
			         .append("<th style='border:1px solid #ddd;padding:8px;'>Name</th>")
			         .append("<th style='border:1px solid #ddd;padding:8px;'>Category</th>")
			         .append("<th style='border:1px solid #ddd;padding:8px;'>Amount</th>")
			         .append("<th style='border:1px solid #ddd;padding:8px;'>Date</th>")
			         .append("</tr>");
			    int i = 1;
			    for (ExpenseDTO expense : todaysExpenses) {
			        table.append("<tr>")
			             .append("<td style='border:1px solid #ddd;padding:8px;text-align:center;'>")
			             .append(i++)
			             .append("</td>")
			             .append("<td style='border:1px solid #ddd;padding:8px;'>")
			             .append(expense.getName())
			             .append("</td>")
			             .append("<td style='border:1px solid #ddd;padding:8px;'>")
			             .append(expense.getCategoryId()!= null ? expense.getCategoryName(): "N/A")
			             .append("</td>")
			             .append("<td style='border:1px solid #ddd;padding:8px;'>")
			             .append(expense.getAmount())
			             .append("</td>")
			             .append("<td style='border:1px solid #ddd;padding:8px;'>")
			             .append(expense.getDate())
			             .append("</td>")
			             .append("</tr>");
			    }

			    // Table end
			    table.append("</table>");
			    String body =
			            "Hi " + profile.getFullNmae() + ",<br/><br/>"
			          + "Here is a summary of your expenses for today:<br/><br/>"
			          + table
			          + "<br/><br/>Best regards,<br/>"
			          + "Money Manager Team";
			    emailService.sendEmail(profile.getEmail(), "Your daily Expenses summary", body);
			}
			
		}
		log.info("job completed: sendDailyExpenseSummary() ");
	}

}
