package in.cozynest.cozyapis.rest.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import in.cozynest.cozyapis.annotations.AdminPath;
import in.cozynest.cozyapis.annotations.UserAdminPath;
import in.cozynest.cozyapis.exception.InternalServerErrorException;
import in.cozynest.cozyapis.exception.NotAcceptableException;
import in.cozynest.cozyapis.exception.NotFoundException;
import in.cozynest.cozyapis.model.CancelledSubscriptionDate;
import in.cozynest.cozyapis.model.CancelledSubscriptionDate.CancelShift;
import in.cozynest.cozyapis.model.Order;
import in.cozynest.cozyapis.model.OrderedSubscription.Shift;
import in.cozynest.cozyapis.model.Subscription;
import in.cozynest.cozyapis.model.Subscription.Status;
import in.cozynest.cozyapis.model.SubscriptionPlan.PlanDuration;
import in.cozynest.cozyapis.model.SubscriptionPlan.PlanType;
import in.cozynest.cozyapis.model.User;
import in.cozynest.cozyapis.rest.ISubscriptionRest;
import in.cozynest.cozyapis.service.ISubscriptionService;
import in.cozynest.cozyapis.service.impl.CancelledSubscriptionDateServiceImpl;
import in.cozynest.cozyapis.service.impl.OrderServiceImpl;
import in.cozynest.cozyapis.service.impl.SubscriptionServiceImpl;
import in.cozynest.cozyapis.utils.DailyCommingStatusWrapper;

@Path("subscription")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SubscriptionRestImpl implements ISubscriptionRest {

	ISubscriptionService subscriptionService = new SubscriptionServiceImpl();

	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

	@POST
	@Path("create")
	@UserAdminPath
	public Subscription create(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate,
			@QueryParam("status") Status status, @QueryParam("orderId") int orderId,
			@QueryParam("address") String address, @QueryParam("startShift") Shift startShift, @QueryParam("endShift") Shift endShift,
			@QueryParam("planDuration") PlanDuration planDuration) {
		if (startDate.equals("") || endDate.equals("") || address.equals(""))
			throw new NotAcceptableException("All feilds required");
		Order order = new OrderServiceImpl().findById(orderId);
		if (order == null)
			throw new InternalServerErrorException("Unable to add new subscription for this user");

		Subscription subscription = new Subscription();
		subscription.setOrder(order);
		subscription.setAddress(address);
		subscription.setStartShift(startShift);
		subscription.setEndShift(endShift);
		try {
			Date newEndDate = formatter.parse(endDate);
			newEndDate = new Date(newEndDate.getYear(), newEndDate.getMonth(), newEndDate.getDate());
			subscription.setEndDate(newEndDate);
			Date newStartDate = formatter.parse(startDate);
			newStartDate = new Date(newStartDate.getYear(), newStartDate.getMonth(), newStartDate.getDate());
			subscription.setStartDate(newStartDate);
		} catch (ParseException pe) {
			System.out.println("SubscriptionRestImpl [public Subscription create()] : " + pe);
			throw new InternalServerErrorException("Invalid endDate/startDate");
		}

		return subscriptionService.createNewSubscription(subscription);
	}

	@PUT
	@Path("update")
	@AdminPath
	public Subscription update(@QueryParam("id") int id, @QueryParam("status") Status status) {
		Subscription subscription = subscriptionService.findById(id);
		if (subscription == null)
			throw new NotFoundException("No subscription found of this id");
		subscription.setId(id);
		subscription.setStatus(status);
		Subscription updatedSubscription = subscriptionService.update(subscription);
		if (updatedSubscription == null)
			throw new InternalServerErrorException("Unable to update subscription");
		return updatedSubscription;
	}

	@GET
	@Path("findall")
	public ArrayList<Subscription> findAll() {
		ArrayList<Subscription> subscription = subscriptionService.findAll();
		if (subscription == null)
			throw new NotFoundException("No record found");

		return subscription;
	}

	@GET
	@Path("findbyid/{id}")
	@UserAdminPath
	public Subscription findById(@PathParam("id") int id) {
		Subscription subscription = subscriptionService.findById(id);
		if (subscription == null)
			throw new NotFoundException("No record found for this id");

		return subscription;
	}

	@GET
	@Path("findbystatus/{status}")
	@UserAdminPath
	public ArrayList<Subscription> findByStatus(@PathParam("status") String status) {
		ArrayList<Subscription> subscription = subscriptionService.findByStatus(status);

		if (subscription == null)
			throw new NotFoundException("No record found for this status");

		return subscription;
	}

	@GET
	@Path("findbystartDate/{startDate}")
	@UserAdminPath
	public ArrayList<Subscription> findByStartDate(@PathParam("startDate") String startDate) {
		ArrayList<Subscription> subscription = subscriptionService.findByStartDate(startDate);
		if (subscription == null)
			throw new NotFoundException("No record found for this startDate");

		return subscription;
	}

	@GET
	@Path("findbyendDate/{endDate}")
	@UserAdminPath
	public ArrayList<Subscription> findByEndDate(@PathParam("endDate") String endDate) {
		ArrayList<Subscription> subscription = subscriptionService.findByEndDate(endDate);
		if (subscription == null)
			throw new NotFoundException("No record found for this endDate");

		return subscription;
	}

	@GET
	@Path("findbyplantype/{planType}")
	@UserAdminPath
	public ArrayList<Subscription> findByPlanType(@PathParam("planType") String planType) {
		ArrayList<Subscription> subscription = subscriptionService.findByPlanType(planType);
		if (subscription == null)
			throw new NotFoundException("No record found for this planType");

		return subscription;
	}

	@GET
	@Path("userId/{userId}")
	@UserAdminPath
	public ArrayList<Subscription> findByUserId(@PathParam("userId") int userId) {
		ArrayList<Subscription> subscription = subscriptionService.findByUserId(userId);

		if (subscription == null)
			throw new NotFoundException("No record found for this userId");

		return subscription;
	}

	@GET
	@Path("findbygenerateduserid/{generatedUserId}")
	@UserAdminPath
	public ArrayList<Subscription> findByGeneratedUserId(@PathParam("generatedUserId") String generatedUserId) {
		ArrayList<Subscription> subscription = subscriptionService.findByGeneratedUserId(generatedUserId);

		if (subscription == null)
			throw new NotFoundException("No record found for this generatedId");

		return subscription;
	}

	@GET
	@Path("findbyphone/{phone}")
	@UserAdminPath
	public ArrayList<Subscription> findByPhone(@PathParam("phone") String phone) {
		ArrayList<Subscription> subscription = subscriptionService.findByPhone(phone);

		if (subscription == null)
			throw new NotFoundException("No record found for this phone");

		return subscription;
	}

	@GET
	@Path("findbyorderId/{orderId}")
	@UserAdminPath
	public ArrayList<Subscription> findByOrderId(@PathParam("orderId") int orderId) {
		ArrayList<Subscription> subscription = subscriptionService.findByOrderId(orderId);
		if (subscription == null)
			throw new NotFoundException("No record found for this orderId");

		return subscription;

	}

	@GET
	@Path("findbytodaysdeliveries")
	@UserAdminPath
	public ArrayList<Subscription> findTodaysDeliveriesByCurrentDateAndCurrentShift(@QueryParam("date") String date,
			@QueryParam("subscriptionShift") String subscriptionShift, @QueryParam("cancelShift") String cancelShift) {
		ArrayList<Subscription> subscription = subscriptionService
				.findTodaysDeliveriesByCurrentDateAndCurrentShift(date, subscriptionShift, cancelShift);
		if (subscription == null)
			throw new NotFoundException("No record found for this todaysdeliverires");

		return subscription;
	}

	@GET
	@Path("dailyCommingStatus/{subscriptionId}")
	@UserAdminPath
	public DailyCommingStatusWrapper findDailyCommingStatus(@PathParam("subscriptionId") int subscriptionId) {
		Subscription subscription = subscriptionService.findById(subscriptionId);
		if (subscription == null)
			throw new NotFoundException("No subscription found with this subscriptionId :" + subscriptionId);
		DailyCommingStatusWrapper dailyCommingStatusWrapper = new DailyCommingStatusWrapper();
		ArrayList<CancelledSubscriptionDate> cancelledSubscriptionDates = new CancelledSubscriptionDateServiceImpl()
				.findBySubscriptionId(subscriptionId);
		ArrayList<String> cancelledDates = new ArrayList<String>();
		for (int i = 0; i < cancelledSubscriptionDates.size(); i++) {
			Date cancelledDate = cancelledSubscriptionDates.get(i).getCancelDate();
			CancelShift cancelShift = cancelledSubscriptionDates.get(i).getCancelShift();
			String cancelledDateAndShift = cancelledDate.getDate() + "-" + (cancelledDate.getMonth() + 1) + "-"
					+ (cancelledDate.getYear() + 1900);
			cancelledDateAndShift += "/" + cancelShift;
			cancelledDates.add(cancelledDateAndShift);
		}
		dailyCommingStatusWrapper.setCancelledSubscriptiondates(cancelledDates);
		ArrayList<Date> datesDifference = getDaysBetweenDates(subscription.getStartDate(), subscription.getEndDate());
		PlanType planType = subscription.getSubscriptionPlan().getPlanType();
		ArrayList<String> dailyCommingDates = new ArrayList<String>();
		String dateAndShift = "";
		String endDate = subscription.getEndDate().getDate() + "-" + (subscription.getEndDate().getMonth() + 1) + "-"
				+ (subscription.getEndDate().getYear() + 1900);
		if (planType == PlanType.LUNCHDINNER) {
			int i = 0;
			if (subscription.getStartShift() == Shift.DINNER) {
				dateAndShift = datesDifference.get(i).getDate() + "-" + (datesDifference.get(i).getMonth() + 1) + "-"
						+ (datesDifference.get(i).getYear() + 1900);
				dateAndShift += "/" + "DINNER";
				dailyCommingDates.add(dateAndShift);
				i++;
			}
			for (; i < datesDifference.size(); i++) {
				String date = datesDifference.get(i).getDate() + "-" + (datesDifference.get(i).getMonth() + 1) + "-"
						+ (datesDifference.get(i).getYear() + 1900);
				dateAndShift = date + "/" + "LUNCH";
				dailyCommingDates.add(dateAndShift);
				if (date.equals(endDate) && subscription.getStartShift() == Shift.DINNER)
					break;
				dateAndShift = date + "/" + "DINNER";
				dailyCommingDates.add(dateAndShift);
			}
		} else {
			for (int i = 0; i < datesDifference.size(); i++) {
				String date = datesDifference.get(i).getDate() + "-" + (datesDifference.get(i).getMonth() + 1) + "-"
						+ (datesDifference.get(i).getYear() + 1900);
				dateAndShift = date + "/" + planType;
				dailyCommingDates.add(dateAndShift);
			}
		}
		dailyCommingStatusWrapper.setDailyCommingDates(dailyCommingDates);
		dailyCommingStatusWrapper.setPlanType(planType);

		Date today = new Date();
		String currentDate = today.getDate() + "-" + (today.getMonth() + 1) + "-" + (today.getYear() + 1900);
		Shift currentShift = today.getHours() < 14 ? Shift.LUNCH : Shift.DINNER;
		dailyCommingStatusWrapper.setCurrentDate(currentDate);
		dailyCommingStatusWrapper.setCurrentShift(currentShift);
		return dailyCommingStatusWrapper;
	}

	private ArrayList<Date> getDaysBetweenDates(Date startdate, Date enddate) {
		ArrayList<Date> dates = new ArrayList<Date>();
		Calendar calendar = new GregorianCalendar();
		String startDate = startdate.getDate() + "-" + (startdate.getMonth() + 1) + "-" + (startdate.getYear() + 1900);
		String endDate = (enddate.getDate() + 1) + "-" + (enddate.getMonth() + 1) + "-" + (enddate.getYear() + 1900);
		try {
			calendar.setTime(formatter.parse(startDate));

			while (calendar.getTime().before(formatter.parse(endDate))) {
				Date result = calendar.getTime();
				dates.add(result);
				calendar.add(Calendar.DATE, 1);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return dates;
	}

	@Override
	@GET
	@Path("findByEndDateAndEndShift")
	@UserAdminPath
	public ArrayList<User> findByEndDateAndEndShift(@QueryParam("endDate") String endDate,
			@QueryParam("endShift") String endShift) {
		ArrayList<User> user = subscriptionService.findByEndDateAndEndShift(endDate, endShift);
		if (user == null)
			throw new NotFoundException("No record found for this EndDateAndEndShift");

		return user;
	}

	

	@Override
	@PUT
	@Path("updateStatus")
	public Subscription updateStatus(@QueryParam("id") int id, @QueryParam("status") Status status) {
		Subscription subscription = subscriptionService.findById(id);
		if(subscription == null)
			throw new NotFoundException("No subscription foundwith this id");
		Subscription updatedSubscription = subscriptionService.update(subscription);
		if(updatedSubscription == null)
			throw new InternalServerErrorException("Unable to update status");
		return updatedSubscription;
	}

	@Override
	@GET
	@Path("findbyplanduration/{planDuration}")
	public ArrayList<Subscription> findByPlanDuration(@PathParam("planDuration") String planDuration) {
		ArrayList<Subscription> subscriptions = subscriptionService.findByPlanDuration(planDuration);
		return subscriptions;
	}
    
}