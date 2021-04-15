import { ChannelAddress, Edge, EdgeConfig } from '../../../shared/shared';
import { Component } from '@angular/core';
import { StorageModalComponent } from './modal/modal.component';
import { AbstractFlatWidget } from '../flat/abstract-flat-widget';
import { CurrentData } from "src/app/shared/shared";

@Component({
    selector: 'storage',
    templateUrl: './storage.component.html'
})
export class StorageComponent extends AbstractFlatWidget {

    public essComponents: EdgeConfig.Component[] = [];
    public chargerComponents: EdgeConfig.Component[] = [];
    public channelAddresses: ChannelAddress[] = [];
    public storageItem: string = null;

    protected getChannelAddresses() {
        for (let component of this.essComponents) {
            this.channelAddresses = [new ChannelAddress(component.id, 'Soc'),
            new ChannelAddress(component.id, 'ActivePower'),
            new ChannelAddress(component.id, 'Capacity')];
        }
        this.channelAddresses.push(new ChannelAddress('_sum', 'EssSoc'));
        return this.channelAddresses
    }
    /**
     * 
     * @param value the value from passed value in html
     * @returns converted value
     */
    public changeWattInKiloWatt = (value: any): string => {
        let thisValue = (value / 1000);
        if (thisValue.toString().endsWith('0')) {
            return thisValue.toString() + ' kW'
        } else {
            return thisValue.toFixed(1).replace('.', ',') + ' kW'
        }
    }

    protected onCurrentData(currentData: CurrentData) {
        this.essComponents = this.config.getComponentsImplementingNature("io.openems.edge.ess.api.SymmetricEss").filter(component => !component.factoryId.includes("Ess.Cluster") && component.isEnabled);
        console.log("essComponentrs", this.essComponents)
        let soc = currentData.allComponents['_sum' + '/EssSoc'];
        if (soc < 20) {
            this.storageItem = 'assets/img/storage_20.png'
        } else if (soc < 40 || soc == 20) {
            this.storageItem = 'assets/img/storage_40.png'
        } else if (soc < 60 || soc == 40) {
            this.storageItem = 'assets/img/storage_60.png'
        } else if (soc < 80 || soc == 60) {
            this.storageItem = 'assets/img/storage_80.png'
        } else if (soc < 100 || soc == 80) {
            this.storageItem = 'assets/img/storage_100.png'
        } else {
            this.storageItem = 'assets/img/storage_100.png'
        }
    }

    async presentModal() {
        const modal = await this.modalController.create({
            component: StorageModalComponent,
            componentProps: {
                edge: this.edge,
                config: this.config,
                essComponents: this.essComponents,
                chargerComponents: this.chargerComponents,
            }
        });
        return await modal.present();
    }
}
