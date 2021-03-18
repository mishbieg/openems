package io.openems.edge.iooffgridswitch;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.metatype.annotations.Designate;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.types.ChannelAddress;
import io.openems.edge.common.channel.BooleanReadChannel;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.io.offgridswitch.api.OffGridSwitch;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Io.Off.Grid.Switch", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE, //
		property = { //
				EventConstants.EVENT_TOPIC + "=" + EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE //
		} //
)
public class IoOffGridSwitch extends AbstractOpenemsComponent implements OffGridSwitch, OpenemsComponent, EventHandler {
	private ChannelAddress mainContactorChannelAddr;
	private ChannelAddress groundingContactorChannelAddr;
	private ChannelAddress gridStatusChannelAddr;

//	protected Optional<Boolean> mainContactor;
//	protected Optional<Boolean> gridDetector;
//	protected Optional<Boolean> grounding;

	@Reference
	protected ComponentManager componentManager;

	private Config config = null;

	public IoOffGridSwitch() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				OffGridSwitch.ChannelId.values()//
		);
	}

	@Activate
	void activate(ComponentContext context, Config config) throws OpenemsNamedException {
		super.activate(context, config.id(), config.alias(), config.enabled());
		this.config = config;
		this.mainContactorChannelAddr = ChannelAddress.fromString(this.config.digitalInput1());
		this.groundingContactorChannelAddr = ChannelAddress.fromString(this.config.digitalInput2());
		this.gridStatusChannelAddr = ChannelAddress.fromString(this.config.digitalInput3());
	}

	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	public void handleEvent(Event event) {
		if (!this.isEnabled()) {
			return;
		}
		switch (event.getTopic()) {
		case EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE:
			this.handleInputOutput();
			break;
		}
	}

	public void handleInputOutput() {
		BooleanReadChannel inChannel1, inChannel2, inChannel3;
		try {
			inChannel1 = this.componentManager.getChannel(mainContactorChannelAddr);
			this._setMainContactor(inChannel1.value().getOrError());
			inChannel2 = this.componentManager.getChannel(gridStatusChannelAddr);
			this._setGridStatus(inChannel2.value().getOrError());
			inChannel3 = this.componentManager.getChannel(groundingContactorChannelAddr);
			this._setGroundingContactor(inChannel3.value().getOrError());
		} catch (IllegalArgumentException | OpenemsNamedException e) {
			e.printStackTrace();
		}
	}
}